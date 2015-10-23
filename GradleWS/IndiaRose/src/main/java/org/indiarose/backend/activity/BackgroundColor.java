package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.R;
import org.indiarose.lib.AppData;
import org.indiarose.lib.view.IndiagramView;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class BackgroundColor extends Activity implements OnClickListener {
	protected static final String OK = "ok";
	protected static final String CANCEL = "cancel";

	protected static final int COLOR_TOP = 1;
	protected static final int COLOR_BOTTOM = 2;

	protected int m_topColor = 0;
	protected int m_bottomColor = 0;
	protected int m_topSize = 0;
	protected boolean active = false;

	ViewGroup leftLayout;
	ViewGroup rightLayout;

	View topArea;
	View bottomArea;

	View okButton;
	View cancelButton;

	Spinner spinner;
	boolean spinnerInitialised = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background_color);
		AppData.current_activity = this;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ajouterVues();
		ajouterListeners();
		charger();
	}

	protected void ajouterVues() {
		leftLayout = (ViewGroup) findViewById(R.id.background_color_layout_left);
		leftLayout = (ViewGroup) findViewById(R.id.background_color_layout_right);

		topArea = findViewById(R.id.background_color_top_area);
		bottomArea = findViewById(R.id.background_color_bottom_area);

		okButton = findViewById(R.id.background_color_button_ok);
		cancelButton = findViewById(R.id.background_color_button_cancel);

		spinner = (Spinner) findViewById(R.id.background_color_spinner_color_size);
	}

	protected void ajouterListeners() {
		topArea.setOnClickListener(this);
		bottomArea.setOnClickListener(this);

		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	protected void charger() {
		m_topColor = AppData.settings.backgroundSelectionArea;
		m_bottomColor = AppData.settings.backgroundSentenceArea;
		m_topSize = AppData.settings.heightSelectionArea;

		System.out.println(m_topColor + " " + m_bottomColor + " " + m_topSize);

		refreshArea();
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		remplirSpinner();
		refreshArea();
	}

	protected void remplirSpinner() {
		if (!spinnerInitialised) {
			spinnerInitialised = true;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item);

		int minHeight = (int) ((IndiagramView.getDefaultHeight() *1.2)
				/ getWindow().getDecorView().getHeight() * 100);
			// int maxHeight = 100 - minHeight;
			int maxHeight = 95 - minHeight;
			int current = 0;

			for (int i = minHeight; i <= maxHeight; ++i) {
				if (i == AppData.settings.heightSelectionArea) {
					current = i - minHeight;
				}
				adapter.add("" + i + "%");
			}

			spinner.setAdapter(adapter);
			spinner.setSelection(current);

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> _parent, View _view,
						int _pos, long _id) {
					String tmp = ((TextView) _view).getText().toString();
					tmp = tmp.substring(0, tmp.length() - 1);
					m_topSize = Integer.parseInt(tmp);
					refreshArea();
				}

				public void onNothingSelected(AdapterView<?> _parent) {

				}
			});
		}
	}

	protected void refreshArea() {
		topArea.setBackgroundColor(m_topColor);
		bottomArea.setBackgroundColor(m_bottomColor);

		int height = leftLayout.getHeight();
		int topHeight = (int) (height * (m_topSize / 100.0));
		int bottomHeight = height - topHeight;

		ViewGroup.LayoutParams top = topArea.getLayoutParams();
		top.height = topHeight;
		topArea.setLayoutParams(top);

		ViewGroup.LayoutParams bottom = bottomArea.getLayoutParams();
		bottom.height = bottomHeight;
		bottomArea.setLayoutParams(bottom);

		/*
		 * m_topArea.setMinimumHeight(topHeight);
		 * m_bottomArea.setMinimumHeight(bottomHeight);
		 * 
		 * 
		 * 
		 * m_topArea.measure(m_topArea.getWidth(), topHeight);
		 * m_bottomArea.measure(m_bottomArea.getWidth(), bottomHeight);
		 */
	}

	// Color picker

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.background_color_top_area: {

			if (!active) {
				active = true;
				final ColorPickerDialog colorDialog = new ColorPickerDialog(
						this, m_topColor);

				colorDialog.setAlphaSliderVisible(false);
				colorDialog.setTitle(getString(R.string.chooseColor));
				colorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								m_topColor = colorDialog.getColor();
								refreshArea();
								active = false;
							}
						});

				colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Nothing to do here.
								active = false;
							}
						});

				colorDialog.show();
			}
		}

			break;

		case R.id.background_color_bottom_area:

		{
			if (!active) {
				active = true;
				final ColorPickerDialog colorDialog = new ColorPickerDialog(
						this, m_bottomColor);

				colorDialog.setAlphaSliderVisible(false);
				colorDialog.setTitle(getString(R.string.chooseColor));
				colorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								active = false;
								m_bottomColor = colorDialog.getColor();
								refreshArea();
							}
						});

				colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								active = false;
								// Nothing to do here.
							}
						});

				colorDialog.show();
			}
		}
			break;

		case R.id.background_color_button_ok:
			AppData.settings.backgroundSelectionArea = m_topColor;
			AppData.settings.backgroundSentenceArea = m_bottomColor;
			AppData.settings.heightSelectionArea = m_topSize;
			AppData.settingsChanged();
			onBackPressed();
			break;

		case R.id.background_color_button_cancel:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, AppSettings.class);
		startActivity(i);
		finish();
	}
}