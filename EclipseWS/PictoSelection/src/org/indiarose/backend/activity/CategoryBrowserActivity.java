package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.TitleBar;
import org.indiarose.lib.AppData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.voice.VoiceActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CategoryBrowserActivity extends VoiceActivity 
{

	protected ScreenManager screen = null;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics windowSize = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(windowSize);

		AppData.currentContext = this;

		LinearLayout windowLayout = new LinearLayout(this);
		RelativeLayout titleLayout = new RelativeLayout(this);
		FrameLayout screenLayout = new FrameLayout(this);

		windowLayout.setOrientation(LinearLayout.VERTICAL);
		windowLayout.addView(titleLayout);
		windowLayout.addView(screenLayout);

		titleLayout.setMinimumHeight(60);
		titleLayout.setMinimumWidth(windowSize.widthPixels);
		titleLayout.measure(windowSize.widthPixels, 60);

		screenLayout.setMinimumHeight(windowSize.heightPixels - 60);
		screenLayout.setMinimumWidth(windowSize.widthPixels);
		screenLayout.measure(windowSize.widthPixels, windowSize.heightPixels - 60);

		windowLayout.setMinimumHeight(windowSize.heightPixels);
		windowLayout.setMinimumWidth(windowSize.widthPixels);
		windowLayout.measure(windowSize.widthPixels, windowSize.heightPixels);

		titleLayout.setBackgroundColor(Color.WHITE);
		screenLayout.setBackgroundColor(Color.WHITE);
		windowLayout.setBackgroundColor(Color.WHITE);

		@SuppressWarnings("unused")
		TitleBar title = new TitleBar(titleLayout);
		screen = new ScreenManager(this, screenLayout, windowSize.widthPixels, windowSize.heightPixels - 60);
		ScreenManager.voiceReader = this.m_voiceReader;
		this.setContentView(windowLayout);
	}

	protected void sendResult(Indiagram _indiagram)
	{
		if(_indiagram instanceof Category){
			//TODO
			Intent returnIntent = new Intent();
			AddIndiagram.category_request = ((Category)_indiagram);
			setResult(Activity.RESULT_OK ,returnIntent);     
			finish();

		}
	}
		
}
