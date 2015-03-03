package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;
import java.util.Stack;

import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.view.element.AbstractScreen;
import org.indiarose.backend.view.element.ButtonItem;
import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.utils.ImageManager;
import org.indiarose.lib.view.EventResult;
import org.indiarose.lib.view.IndiagramView;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.ImageView.ScaleType;

/**
 * Signal : - selected (Category) : signal raised when a category is selected.
 */
public class CategoryBrowserForActivity extends AbstractScreen {
	protected static final String BACK = "back";
	protected static final String NEXT = "next";

	protected LinearLayout m_windowLayout = null;
	protected RelativeLayout m_browserLayout = null;
	protected AlertDialog m_currentDialog = null;
	protected int m_height = 0;

	protected ArrayList<Indiagram> m_ignoreList = new ArrayList<Indiagram>();

	protected IndiagramView[][] m_displayableView = null;
	protected Stack<Category> m_categoriesStack = new Stack<Category>();
	protected ArrayList<IndiagramView> m_currentViews = new ArrayList<IndiagramView>();
	protected int m_currentViewOffset = 0;
	protected ButtonItem[] buttons;
	CategoryBrowserActivity activity;

	public CategoryBrowserForActivity(ScreenManager _screen,
			CategoryBrowserActivity activity) {
		super(_screen);

		this.activity = activity;

		if (activity != null && activity.getIntent() != null
				&& activity.getIntent().getExtras() != null) {
			Bundle extras = activity.getIntent().getExtras();
			if (extras.containsKey("ignore")) {
				addToIgnoreList(EditIndiagram.ignore);
			}
		}

		m_windowLayout = new LinearLayout(AppData.currentContext);
		m_browserLayout = new RelativeLayout(AppData.currentContext);
		LinearLayout buttonLayout = new LinearLayout(AppData.currentContext);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER);
		buttonLayout.setMinimumWidth(this.m_screen.getWidth());
		buttonLayout
				.setMinimumHeight(this.m_screen.getHeight()
						- (int) (this.m_screen.getHeight() * (AppData.settings.heightSelectionArea / 100.0)));

		this.m_height = (int) (this.m_screen.getHeight() * (AppData.settings.heightSelectionArea / 100.0));
		m_browserLayout.setMinimumHeight(m_height);
		m_browserLayout.setMinimumWidth(this.m_screen.getWidth());
		m_browserLayout.setBackgroundResource(R.color.bleu_clair);

		ButtonItem backButton = new ButtonItem(AppData.currentContext, BACK);
		backButton.setBackgroundResource(R.drawable.buttonblue);

		ButtonItem nextButton = new ButtonItem(AppData.currentContext, NEXT);
		nextButton.setBackgroundResource(R.drawable.buttongreen);

		buttons = new ButtonItem[] { backButton, nextButton };
		int[] resId = new int[] { R.string.backText, R.string.nextText };
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(250, 75);
		lp.rightMargin = 4;
		for (int i = 0; i < buttons.length; ++i) {
			ButtonItem b = buttons[i];
			b.setWidth(250);
			b.setHeight(80);
			b.setTextSize(15);
			b.setTextColor(Color.WHITE);
			b.setText(resId[i]);
			try {
				Mapper.connect(b, "clicked", this, "buttonEvent");
			} catch (MapperException ex) {
				Log.wtf("IndiagramBrowser",
						"unable to connect button " + b.getIdentifier(), ex);
			}
			buttonLayout.addView(b, lp);
		}

		m_windowLayout.setOrientation(LinearLayout.VERTICAL);
		m_windowLayout.addView(m_browserLayout);
		m_windowLayout.addView(buttonLayout);

		initBrowser();
	}

	public void addToIgnoreList(Indiagram _indiagram) {
		this.m_ignoreList.add(_indiagram);
	}

	protected void initBrowser() {
		int indiagramByLine = this.m_screen.getWidth()
				/ IndiagramView.getDefaultWidth();
		int numberOfLine = this.m_height / IndiagramView.getDefaultHeight();

		m_displayableView = new IndiagramView[numberOfLine][];
		for (int i = 0; i < numberOfLine; ++i) {
			m_displayableView[i] = new IndiagramView[indiagramByLine];
		}
	}

	@Override
	public View getView() {
		return m_windowLayout;
	}

	public void buttonEvent(ButtonItem _button) {
		String id = _button.getIdentifier();
		if (id.equals(BACK)) {
			this.m_screen.pop();
		} else if (id.equals(NEXT)) {
			if (isMoreView()) {
				nextIndiagram();
			} else {
				popCategory();
			}
		}
	}

	protected void reset() {
		if (buttons != null) {
			for (ButtonItem b : buttons)
				Mapper.disconnect(b, "clicked", this, "buttonEvent");

		}
		synchronized (this) {

			this.m_categoriesStack.clear();
			this.m_browserLayout.removeAllViews();
			clearViewList();
		}
	}

	@Override
	public void onPush() {
		pushCategory(AppData.homeCategory);
	}

	@Override
	public void onPop() {
		reset();
	}

	protected void clearViewList() {
		for (int i = 0; i < this.m_currentViews.size(); ++i) {
			Mapper.disconnect(this.m_currentViews.get(i));
		}
		this.m_currentViews.clear();
	}

	public void popCategory() {
		if (this.m_categoriesStack.size() > 1) {
			this.m_categoriesStack.pop();
			pushCategory(this.m_categoriesStack.pop());
		} else {
			this.m_currentViewOffset = 0;
			nextIndiagram();
		}
	}

	public synchronized void pushCategory(Category _category) {
		synchronized (this) {
			this.m_categoriesStack.push(_category);
			clearViewList();
			this.m_currentViewOffset = 0;

			ArrayList<Indiagram> indiagrams = new ArrayList<Indiagram>();
			indiagrams.addAll(_category.indiagrams);

			int size = indiagrams.size();
			int textColor = _category.textColor;
			IndiagramView v = null;
			int id = 0x2A;
			for (int i = 0; i < size; ++i) {
				v = indiagrams.get(i).getView();
				v.setId(id++);
				v.setTextColor(textColor);
				this.m_currentViews.add(v);

				try {
					Mapper.connect(v, "touchEvent", this, "indiagramEvent");
				} catch (Exception ex) {
					Log.wtf("IndiagramBrowser", ex);
				}
			}
		}
		nextIndiagram();

		try {
			Mapper.emit(this, "categoryChanged", _category);
		} catch (MapperException e) {
			Log.wtf("IndiagramBrowser", e);
		}
	}

	public void nextIndiagram() {
		synchronized (this) {
			this.m_browserLayout.removeAllViews();

			for (int i = 0; i < m_displayableView.length; ++i) {
				for (int j = 0; j < m_displayableView[i].length; ++j) {
					m_displayableView[i][j] = null;
				}
			}

			int index = this.m_currentViewOffset;

			for (int i = 0; i < m_displayableView.length; ++i) {
				for (int j = 0; j < m_displayableView[i].length
						&& index < m_currentViews.size(); ++j) {
					IndiagramView v = m_currentViews.get(index++);
					while ((!(v.getIndiagram() instanceof Category) || this.m_ignoreList
							.contains(v.getIndiagram()))
							&& index < m_currentViews.size()) {
						v = m_currentViews.get(index++);
					}
					if (!(v.getIndiagram() instanceof Category)
							|| this.m_ignoreList.contains(v.getIndiagram())) {
						break;
					}

					m_displayableView[i][j] = v;
				}
			}

			boolean stop = false;
			int maxOfPreviousLine = -1;
			int maxOfLine = -1;

			int currentHeight = 0;
			for (int i = 0; i < m_displayableView.length; ++i) {
				maxOfPreviousLine = maxOfLine;
				maxOfLine = -1;
				for (int j = 0; j < m_displayableView[i].length; ++j) {
					if (m_displayableView[i][j] == null) {
						stop = true;
						break;
					}

					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					if (i == 0) {
						param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					} else {
						param.addRule(RelativeLayout.BELOW,
								m_displayableView[i - 1][maxOfPreviousLine]
										.getId());
					}
					if (j == 0) {
						param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					} else {
						param.addRule(RelativeLayout.RIGHT_OF,
								m_displayableView[i][j - 1].getId());
					}
					this.m_browserLayout
							.addView(m_displayableView[i][j], param);

					this.m_currentViewOffset = this.m_currentViews
							.indexOf(m_displayableView[i][j]) + 1;
					if (maxOfLine == -1
							|| m_displayableView[i][maxOfLine].getRealHeight() < m_displayableView[i][j]
									.getRealHeight()) {
						maxOfLine = j;
					}
				}

				if (maxOfLine >= 0) {
					currentHeight += m_displayableView[i][maxOfLine]
							.getRealHeight();

					if (currentHeight > this.m_height) {
						stop = true;
						for (int j = 0; j < m_displayableView[i].length
								&& m_displayableView[i][j] != null; ++j) {
							this.m_browserLayout
									.removeView(m_displayableView[i][j]);
							this.m_currentViewOffset--;
						}
					}
				}

				if (stop) {
					break;
				}
			}
		}
	}

	public boolean isMoreView() {
		if (this.m_currentViewOffset < this.m_currentViews.size()) {
			for (int i = this.m_currentViewOffset; i < this.m_currentViews
					.size(); ++i) {
				if (this.m_currentViews.get(i).getIndiagram() instanceof Category
						&& !this.m_ignoreList.contains(this.m_currentViews.get(
								i).getIndiagram())) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean hasChild(Category _indiagram) {
		for (int i = 0; i < _indiagram.indiagrams.size(); ++i) {
			if (_indiagram.indiagrams.get(i) instanceof Category
					&& !this.m_ignoreList
							.contains(_indiagram.indiagrams.get(i))) {
				return true;
			}
		}
		return false;
	}

	public synchronized void indiagramEvent(IndiagramView _view,
			MotionEvent _event, EventResult _result) {
		if (_event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			final Indiagram indiagram = _view.getIndiagram();
			AlertDialog.Builder adb = new AlertDialog.Builder(
					AppData.currentContext);
			adb.setTitle(R.string.whichActionQuestion);
			adb.setIcon(android.R.drawable.ic_dialog_alert);

			ImageView indiagramImage = new ImageView(AppData.currentContext);
			indiagramImage.setScaleType(ScaleType.CENTER_INSIDE);
			indiagramImage.setMaxHeight(AppData.settings.indiagramSize);
			indiagramImage.setMaxWidth(AppData.settings.indiagramSize);
			indiagramImage.setMinimumHeight(AppData.settings.indiagramSize);
			indiagramImage.setMinimumWidth(AppData.settings.indiagramSize);
			try {
				indiagramImage.setImageBitmap(ImageManager.loadImage(
						PathData.IMAGE_DIRECTORY + indiagram.imagePath,
						AppData.settings.indiagramSize,
						AppData.settings.indiagramSize));
			} catch (Exception e) {
				Log.wtf("IndiagramBrowser", "unable to load image "
						+ indiagram.imagePath, e);
			}
			adb.setView(indiagramImage);

			adb.setPositiveButton(R.string.selectText,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface _dialog, int _which) {
							closeDialog();
							sendSignal(indiagram);
							onPop();
							m_screen.pop();
						}
					});
			if (hasChild((Category) indiagram)) {
				adb.setNeutralButton(R.string.goIntoText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface _dialog,
									int _which) {
								pushCategory((Category) indiagram);
								closeDialog();
							}
						});
			}
			adb.setNegativeButton(R.string.cancelText,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface _dialog, int _which) {
							closeDialog();
						}
					});

			adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface _dialog) {
					closeDialog();
				}
			});
			this.m_currentDialog = adb.show();
		}
	}

	protected void sendSignal(Indiagram _indiagram) {
		activity.sendResult(_indiagram);

		/*
		 * try { Mapper.emit(this, "selected", _indiagram); } catch
		 * (MapperException e) { Log.wtf("CategoryBrowser",
		 * "Error while emitting signal", e); }
		 */
	}

	protected void closeDialog() {
		if (this.m_currentDialog != null) {
			this.m_currentDialog.dismiss();
		}
	}
}
