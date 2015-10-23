package org.indiarose.lib.voice;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class VoiceActivity extends Activity
{
	private VoiceEngine m_voiceEngine = null;
	protected VoiceReader m_voiceReader = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Log.e("VoiceEngine", "onCreate");

		this.m_voiceEngine = new VoiceEngine(this, Locale.getDefault());
		this.m_voiceReader = new VoiceReader(this.m_voiceEngine);


		Log.wtf("VoiceEngine", "onCreateEnd");
	}

	@Override
	public void onDestroy() 
	{
		this.m_voiceEngine.destroy();
		super.onDestroy();

		Log.wtf("VoiceEngine", "onDestroy");
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Log.e("R", "onBackPressed");
		onDestroy();
	}

	@Override
	public void onStop()
	{
		super.onStop();;
		Log.e("R", "onBackPressed");
		onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.wtf("VoiceEngine", "onPause");
	}

	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) 
	{
		Log.wtf("VoiceActivity", "onActivityResult");
		this.m_voiceEngine.activityResult(_requestCode, _resultCode, _data);
	}

}
