package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.R;
import org.indiarose.backend.view.element.EnableDisableChoice;
import org.indiarose.backend.view.element.NumberSlider;
import org.indiarose.lib.AppData;
import org.indiarose.lib.model.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class AppSettings extends Activity implements View.OnClickListener
{

	View backgroundColor;
	View indiagramProperty;
	View lecture;
	View reinitialisation;
	View previous;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_app_settings);
		AppData.current_activity = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ajouterVues();
		ajouterListeners();

	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.app_settings_background_properties:
				launchBackgroundColor();
				break;

			case R.id.app_settings_indiagram_properties:
				launchIndiagramProperty();
				break;

			case R.id.app_settings_lecture_properties:
				launchListenProperty();
				break;

			case R.id.app_settings_reinitialisation:
				openResetSettingsDialog();
				break;

			case R.id.app_settings_previous:
				onBackPressed();
				break;
		}
	}


	@Override
	public void onBackPressed() 
	{
		Intent i = new Intent(this, Home.class);
		startActivity(i);
		finish();
	}

	protected void ajouterVues(){
		backgroundColor = findViewById(R.id.app_settings_background_properties);
		indiagramProperty = findViewById(R.id.app_settings_indiagram_properties);
		lecture = findViewById(R.id.app_settings_lecture_properties);
		reinitialisation = findViewById(R.id.app_settings_reinitialisation);
		previous = findViewById(R.id.app_settings_previous);
	}

	protected void ajouterListeners(){
		backgroundColor.setOnClickListener(this);
		indiagramProperty.setOnClickListener(this);
		lecture.setOnClickListener(this);
		reinitialisation.setOnClickListener(this);
		previous.setOnClickListener(this);
	}
	protected void launchBackgroundColor(){
		//this.m_screen.push(new BackgroundColor(this.m_screen));
		Intent i = new Intent(this,BackgroundColor.class);
		startActivity(i);
		finish();
	}

	protected void launchIndiagramProperty(){
		//this.m_screen.push(new IndiaViewProperty(this.m_screen));
		Intent i = new Intent(this,IndiaProperty.class);
		startActivity(i);
		finish();
	}

	protected void launchListenProperty(){
		Intent i = new Intent(this,ListenProperty.class);
		startActivity(i);
		finish();
	}


	/*protected void openCategoryReadingDialog(){
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
	}*/

	protected void openResetSettingsDialog(){
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.resetSettingsQuestion);
		adb.setIcon(android.R.drawable.ic_dialog_alert);
		adb.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface _dialog, int _which) 
			{
				AppData.settings = new Settings();
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

}
