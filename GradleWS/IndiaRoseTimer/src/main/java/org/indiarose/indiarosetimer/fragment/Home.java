package org.indiarose.indiarosetimer.fragment;

import org.indiarose.api.fragments.core.FragmentNormal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import org.indiarose.indiarosetimer.R;
import org.indiarose.indiarosetimer.activity.MainActivity;

public class Home extends FragmentNormal{

	View _facebook;
	View _twitter;
	View _book;
	View _launchTimer;
	View _top_title;
	View _volume ;
	View _settings;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.accueil)){
			ajouterVues();
			ajouterListeners();
			ajusterVues();
			screenOrientation();
		}
		return getFragmentView();
	}
	
	private void screenOrientation() {
		SharedPreferences pref = 	getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
		Editor editor = pref.edit();
		int screen_orientation = pref.getInt("screen_orientation", -1);
		
		if(screen_orientation == -1){
			editor.putInt("screen_orientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			editor.commit(); // commit changes
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			getActivity().setRequestedOrientation(screen_orientation);
		}
	}

	@SuppressWarnings("deprecation")
	private void ajusterVues() {
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		int orientation = getResources().getConfiguration().orientation;

		int taille = 0;

		if(orientation == Configuration.ORIENTATION_PORTRAIT){
			taille = display.getWidth();
			_top_title.setVisibility(View.VISIBLE);
		}else{
			taille = display.getHeight()/2;
		}	

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(taille,taille);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		layoutParams.setMargins(10, 10, 10, 10);
	/*
		_launchTimer.setLayoutParams(layoutParams);

		((RelativeLayout)_launchTimer).setGravity(Gravity.CENTER);
		((RelativeLayout)_launchTimer).setPadding(10, 10, 10, 10);
		*/

	}

	private void ajouterVues() {
		_book = findViewById(R.id.book);
		_facebook = findViewById(R.id.facebook);
		_twitter = findViewById(R.id.twitter);
		_launchTimer = findViewById(R.id.launchTimer);
		_top_title = findViewById(R.id.top_title);
		_volume = findViewById(R.id.volume);
		_settings = findViewById(R.id.settings);
	}

	private void ajouterListeners() {
		_book.setOnClickListener(this);
		_facebook.setOnClickListener(this);
		_twitter.setOnClickListener(this);
		_launchTimer.setOnClickListener(this);
		_volume.setOnClickListener(this);
		_settings.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book:	
			break;
		case R.id.facebook:
			launchPageFacebook();
			break;
		case R.id.twitter:
			launchPageTwitter();
			break;
		
		case R.id.launchTimer:
			launchGridCategory();
			break;
			
		case R.id.volume:
			((MainActivity)getActivity()).launchVolumeManager();
			break;
			
		case R.id.settings:
			launchSettings();
			break;
		}
	}


	private void launchSettings() {
		ajouterFragment(new SettingsApplication(),false);
	}


	private class LaunchTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
				launchGridCategory();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private void launchGridCategory() {
		ajouterFragment(new GridCategoryFragment(), false);
	}

	private void launchPageFacebook() {
		Intent goToMarket = null;
		goToMarket = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://www.facebook.com/pages/India-Rose/403964423070474"));
		startActivity(goToMarket);
	}

	private void launchPageTwitter() {
		Intent goToMarket = null;
		goToMarket = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://twitter.com/IndiaRoseAutism"));
		startActivity(goToMarket);
	}

	protected void tournerRoulette(){
		int previousDegrees = 0;
		int degrees = 1080;
		RotateAnimation animation = new RotateAnimation(previousDegrees, degrees, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setDuration(1000);
		_launchTimer.startAnimation(animation);
	}
}
