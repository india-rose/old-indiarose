package org.indiarose.frontend;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.R;
import org.indiarose.frontend.view.*;
import org.indiarose.lib.*;
import org.indiarose.lib.voice.VoiceActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

@SuppressWarnings("deprecation")
public class PictoSelection extends VoiceActivity implements
		Bootstrap.BootstrapDelegate {
	protected IndiagramBrowser indiagramBrowser = null;
	protected SentenceArea sentenceArea = null;
	protected TitleBar titleBar = null;
	protected InteractionManager interactionManager = null;

	protected AbsoluteLayout selectionWindow = null;

	protected RelativeLayout sentenceLayout = null;
	protected RelativeLayout browserLayout = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (AppData.current_activity != null)
			AppData.current_activity.finish();

		AppData.current_activity = this;

		// on vide les logs si trop volumineux
		IndiaLogger.rmLogsIfTooHuge();

		// set some window parameters.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		load();

	}

	public void creer() {
		// Retrieve screen dimension.
		DisplayMetrics windowSize = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(windowSize);

		AppData.currentContext = this;

		selectionWindow = new AbsoluteLayout(this);
		browserLayout = (RelativeLayout) this.getLayoutInflater().inflate(
				R.layout.relative_layout, null);
		sentenceLayout = (RelativeLayout) this.getLayoutInflater().inflate(
				R.layout.relative_layout, null);
		RelativeLayout titleLayout = (RelativeLayout) this.getLayoutInflater()
				.inflate(R.layout.relative_layout, null);
		LinearLayout sentenceLayoutContainer = (LinearLayout) this
				.getLayoutInflater().inflate(R.layout.linear_layout, null);
		LinearLayout browserLayoutContainer = (LinearLayout) this
				.getLayoutInflater().inflate(R.layout.linear_layout, null);
		LinearLayout titleLayoutContainer = (LinearLayout) this
				.getLayoutInflater().inflate(R.layout.linear_layout, null);

		selectionWindow.setMinimumHeight(windowSize.heightPixels);
		selectionWindow.setMinimumWidth(windowSize.widthPixels);
		selectionWindow
				.measure(windowSize.widthPixels, windowSize.heightPixels);

		int height = windowSize.heightPixels - 60;
		int browserHeight = (int) (height * (AppData.settings.heightSelectionArea / 100.0));
		int sentenceHeight = height - browserHeight;

		titleLayoutContainer.setMinimumHeight(60);
		titleLayoutContainer.setMinimumWidth(windowSize.widthPixels);
		titleLayoutContainer.measure(windowSize.widthPixels, 60);
		titleLayoutContainer.setBackgroundColor(Color.WHITE);

		titleLayout.setLayoutParams(new ViewGroup.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		titleLayout.setBackgroundColor(Color.WHITE);
		titleLayoutContainer.addView(titleLayout);

		browserLayoutContainer.setMinimumWidth(windowSize.widthPixels);
		browserLayoutContainer.setMinimumHeight(browserHeight);
		browserLayoutContainer.measure(windowSize.widthPixels, browserHeight);

		browserLayout.setLayoutParams(new ViewGroup.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		browserLayout
				.setBackgroundColor(AppData.settings.backgroundSelectionArea);

		browserLayoutContainer
				.setBackgroundColor(AppData.settings.backgroundSelectionArea);
		browserLayoutContainer.addView(browserLayout);

		sentenceLayoutContainer.setMinimumWidth(windowSize.widthPixels);
		sentenceLayoutContainer.setMinimumHeight(sentenceHeight);
		sentenceLayoutContainer.measure(windowSize.widthPixels, sentenceHeight);

		sentenceLayout.setLayoutParams(new ViewGroup.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		sentenceLayout
				.setBackgroundColor(AppData.settings.backgroundSentenceArea);

		sentenceLayoutContainer
				.setBackgroundColor(AppData.settings.backgroundSentenceArea);
		sentenceLayoutContainer.addView(sentenceLayout);

		this.sentenceArea = new SentenceArea(sentenceLayout,
				windowSize.widthPixels, this.m_voiceReader);
		this.titleBar = new TitleBar(titleLayout);
		this.indiagramBrowser = new IndiagramBrowser(browserLayout,
				windowSize.widthPixels, browserHeight, this.sentenceArea);
		this.interactionManager = new InteractionManager(this.titleBar,
				this.indiagramBrowser, this.sentenceArea, this.selectionWindow,
				this.m_voiceReader, browserHeight + 60, windowSize);

		this.indiagramBrowser.pushCategory(AppData.homeCategory);

		selectionWindow.addView(titleLayoutContainer,
				new AbsoluteLayout.LayoutParams(windowSize.widthPixels, 60, 0,
						0));
		selectionWindow.addView(sentenceLayoutContainer,
				new AbsoluteLayout.LayoutParams(windowSize.widthPixels,
						sentenceHeight, 0, browserHeight + 60));
		selectionWindow.addView(browserLayoutContainer,
				new AbsoluteLayout.LayoutParams(windowSize.widthPixels,
						browserHeight, 0, 60));

		setContentView(selectionWindow);

		System.gc();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		save();
		finish();

		android.os.Process.killProcess(android.os.Process.myPid());

	}

	public void onDestroy() {
		Log.e("FRONT", "destroy");
		save();
		super.onDestroy();
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	protected void load() {
		try {
			Log.e("BACK", "load");
			// initialize all library class.
			Bootstrap.Initialize(this, this);
		} catch (Exception ex) {
			Log.e("org.indiarose.frontend.PictoSelection",
					"Bootstrap Initialization error", ex);
			this.finish();
		}
	}

	protected void save() {
		try {
			IndiaLogger.flushB4Save();
			Log.e("BACK", "save");
			Bootstrap.Uninitialize(false);
		} catch (Exception ex) {
			Log.wtf("PictoSelection", ex);
		}
	}

	@Override
	public void onBootstrapInitialised() {
		creer();
	}

}