package org.indiarose.backend.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import org.indiarose.R;
import org.indiarose.backend.view.element.EnableDisableChoice;
import org.indiarose.backend.view.element.NumberSlider;
import org.indiarose.lib.AppData;

/**
 * Created by lisa on 28/04/14.
 */
public class ListenProperty extends Activity implements View.OnClickListener
{
	View categoryReading;
	View readingDelay;
	protected View buttonOk = null;
	protected View buttonCancel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_listen_property);
		AppData.current_activity = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ajouterVues();
		ajouterListeners();

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.okButton:
				onBackPressed();
				break;

			case R.id.cancelButton:
				onBackPressed();
				break;
			case R.id.app_settings_category_reading:
				openCategoryReadingDialog();
				break;

			case R.id.app_settings_reading_delay:
				openReadingDelayDialog();
				break;
		}
	}

	protected void ajouterVues(){
		categoryReading = findViewById(R.id.app_settings_category_reading);
		readingDelay = findViewById(R.id.app_settings_reading_delay);
		buttonOk = findViewById(R.id.okButton);
		buttonCancel = findViewById(R.id.cancelButton);
	}

	protected void ajouterListeners(){
		categoryReading.setOnClickListener(this);
		readingDelay.setOnClickListener(this);
		buttonOk.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	protected void openCategoryReadingDialog(){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		final EnableDisableChoice choice = new EnableDisableChoice();
		choice.setValue(AppData.settings.enableCategoryReading);
		adb.setView(choice.getView());
		adb.setTitle(R.string.categoryReadingQuestion);
		adb.setIcon(android.R.drawable.ic_dialog_alert);
		adb.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface _dialog, int _which)
			{
				AppData.settings.enableCategoryReading = choice.getValue();
				AppData.settingsChanged();
			}
		});
		adb.setNegativeButton(R.string.cancelText, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface _dialog, int _which)
			{
				_dialog.dismiss();
			}
		});

		adb.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface _dialog)
			{
				_dialog.dismiss();
			}
		});

		adb.show();
	}

	protected void openReadingDelayDialog(){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		final NumberSlider slider = new NumberSlider();
		slider.setValue(AppData.settings.wordsReadingDelay);

		adb.setView(slider.getView());
		adb.setTitle(R.string.readingDelayQuestion);
		adb.setIcon(android.R.drawable.ic_dialog_alert);
		adb.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface _dialog, int _which)
			{
				AppData.settings.wordsReadingDelay = slider.getValue();
				AppData.settingsChanged();
			}
		});
		adb.setNegativeButton(R.string.cancelText, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface _dialog, int _which)
			{
				_dialog.dismiss();
			}
		});

		adb.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface _dialog)
			{
				_dialog.dismiss();
			}
		});

		adb.show();
	}

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(this, AppSettings.class);
		startActivity(i);
		finish();
	}
}
