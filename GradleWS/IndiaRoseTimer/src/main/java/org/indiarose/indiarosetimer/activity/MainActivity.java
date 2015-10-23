package org.indiarose.indiarosetimer.activity;

import org.indiarose.api.activity.IndiaRoseFragmentActivity;

import android.content.Context;
import android.media.AudioManager;
import android.support.v4.app.Fragment;
import org.indiarose.indiarosetimer.fragment.Home;

public class MainActivity extends IndiaRoseFragmentActivity{

	@Override
	public void onIndiagramManagerInitialised() {
		charger();
	}

	@Override
	protected void ajouterVues() {
	}

	@Override
	protected void charger() {
		if(getFragments().size() == 0)
			ajouterFragment(new Home(), false);
		else{
			Fragment f = getFragments().getLast();
			if(f != null)
				f.onResume();
		}
	}

	public void launchVolumeManager() {
		AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		audio.setStreamVolume(AudioManager.STREAM_ALARM,audio.getStreamVolume(AudioManager.STREAM_ALARM),AudioManager.FLAG_SHOW_UI);
	}
}
