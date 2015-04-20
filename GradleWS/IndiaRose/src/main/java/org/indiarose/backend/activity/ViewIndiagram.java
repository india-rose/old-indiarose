package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;

import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.utils.ImageManager;
import org.indiarose.lib.voice.VoiceActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class ViewIndiagram extends VoiceActivity implements OnClickListener {

	protected static Indiagram m_indiagram = null;
	protected LinearLayout m_layout = null;

	protected TextView m_indiagramText = null;
	protected TextView m_indiagramSound = null;
	protected TextView m_indiagramCategory = null;

	protected Button listenButton = null;
	protected Button backButton = null;
	protected Button editButton = null;
	protected Button deleteButton = null;
	protected ImageView m_indiagramImage = null;
	protected LinearLayout m_indiagramTextColor = null;

	protected AlertDialog m_currentDialog = null;

	protected LinearLayout colorLayout = null;
	protected int idIndiagram = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_view_indiagram);
		AppData.currentContext = this;
		AppData.current_activity = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ScreenManager.voiceReader = this.m_voiceReader;
		m_indiagram = AppData.current_indiagram;
		m_indiagramText = (TextView) findViewById(R.id.text);
		m_indiagramSound = (TextView) findViewById(R.id.m_indiagramSound);
		m_indiagramCategory = (TextView) findViewById(R.id.m_indiagramCategory);

		m_indiagramImage = (ImageView) findViewById(R.id.indiagram);
		m_indiagramImage.setScaleType(ScaleType.CENTER_INSIDE);
		m_indiagramImage.setMaxHeight(AppData.settings.indiagramSize);
		m_indiagramImage.setMaxWidth(AppData.settings.indiagramSize);
		m_indiagramImage.setMinimumHeight(AppData.settings.indiagramSize);
		m_indiagramImage.setMinimumWidth(AppData.settings.indiagramSize);

		listenButton = (Button) findViewById(R.id.listen);
		backButton = (Button) findViewById(R.id.backButton);
		editButton = (Button) findViewById(R.id.editButton);
		deleteButton = (Button) findViewById(R.id.deleteButton);

		listenButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		editButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		m_indiagramTextColor = (LinearLayout) findViewById(R.id.m_indiagramTextColor);
		colorLayout = (LinearLayout) findViewById(R.id.layout_color);
		refresh();
	}

	protected void refresh() {
		m_indiagramText.setText(m_indiagram.text);
		if (TextUtils.isEmpty(m_indiagram.soundPath)) {
			m_indiagramSound.setText(AppData.currentContext
					.getString(R.string.indiagramNoSound));
		} else {
			m_indiagramSound.setText(m_indiagram.soundPath);
		}

		if(m_indiagramCategory != null && Indiagram.getParent(m_indiagram) != null && Indiagram.getParent(m_indiagram).text != null)
			m_indiagramCategory.setText(Indiagram.getParent(m_indiagram).text);

		try {
			m_indiagramImage.setImageBitmap(ImageManager.loadImage(
					PathData.IMAGE_DIRECTORY + m_indiagram.imagePath,
					AppData.settings.indiagramSize,
					AppData.settings.indiagramSize));
		} catch (Exception e) {
			Log.wtf("ViewIndiagram", "unable to load image : "
					+ m_indiagram.imagePath, e);
		}

		if (m_indiagram instanceof Category) {
			colorLayout.setVisibility(LinearLayout.VISIBLE);
			m_indiagramTextColor
			.setBackgroundColor(((Category) m_indiagram).textColor);
		} else {
			colorLayout.setVisibility(LinearLayout.GONE);
		}
	}

	public void changeIndiagram(Indiagram _indiagram) {
		m_indiagram = _indiagram;
	}

	protected void closeDialog() {
		this.m_currentDialog.dismiss();
	}

	protected void delete(Indiagram _indiagram) {
		AppData.changeLog.deleteIndiagram(_indiagram);
		// find parent and delete category
		Indiagram parent = Indiagram.getParent(_indiagram);
		if (parent != null) {
			AppData.changeLog.changeIndiagram(parent);
			((Category) parent).indiagrams.remove(_indiagram);
		}

		File f = new File(PathData.XML_DIRECTORY + _indiagram.filePath);
		f.delete();

		try {
			AppData.SaveHomeCategory();
		} catch (Exception e) {
			Log.wtf("ViewIndiagram", "Unable to save collection", e);
		}
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.listen:
			ScreenManager.voiceReader.read(m_indiagram);
			break;

		case R.id.backButton:
			onBackPressed();
			break;

		case R.id.deleteButton:
			delete();
			break;

		case R.id.editButton:
			i = new Intent(this, EditIndiagram.class);
			startActivity(i);
			finish();
			break;
		default:
			break;
		}
	}

	private void delete() {
		if (m_indiagram instanceof Category) {
			if (((Category) m_indiagram).indiagrams.size() >= 0) {
				AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
				if (((Category) m_indiagram).indiagrams.size() == 0){
					deleteDialog.setMessage(R.string.deleteCategory);
				}
				else{
					deleteDialog.setMessage(R.string.deleteCategoryNotEmpty);
				}
				deleteDialog.setTitle(R.string.deleteQuestion);
				// deleteDialog.setIcon(android.R.drawable.ic_dialog_alert);
				deleteDialog.setPositiveButton(R.string.okText,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface _dialog,
							int _which) {

						// supprimer tous les indiagrams de la categorie
						for (int i = 0; i < ((Category) m_indiagram).indiagrams
								.size(); i++) {
							delete(((Category) m_indiagram).indiagrams
									.get(i));
						}
						delete(m_indiagram);
						Intent i = new Intent(((Dialog) _dialog)
								.getContext(),
								CollectionManagement.class);
						startActivity(i);
					}
				});
				deleteDialog.setNegativeButton(R.string.cancelText,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface _dialog,
							int _which) {
						closeDialog();
					}
				});
				deleteDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface _dialog) {
						closeDialog();
					}
				});
				this.m_currentDialog = deleteDialog.show();
				// Toast.makeText(this, R.string.canNotDeleteCategory,
				// Toast.LENGTH_LONG).show();
			}
		} else {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);

			adb.setTitle(R.string.deleteQuestion);
			adb.setIcon(android.R.drawable.ic_dialog_alert);
			adb.setPositiveButton(R.string.okText,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface _dialog, int _which) {
					delete(m_indiagram);

					Intent i = new Intent(((Dialog) _dialog)
							.getContext(), CollectionManagement.class);
					startActivity(i);
				}
			});
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent i = new Intent(this, CollectionManagement.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		finish();
	}
}
