package org.indiarose.backend.activity;

import org.indiarose.R;
import org.indiarose.lib.AppData;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class InstallTTS extends Activity implements View.OnClickListener {

	View _install_ivona;
	View _install_pack_langue;
	View _launch_intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_install_tts);

		AppData.current_activity = this;

		ajouterVues();
		ajouterListeners();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, Home.class);
		startActivity(i);
		finish();
	}

	private void ajouterVues() {
		_install_ivona = findViewById(R.id.install_ivona_launch_tts);
		_install_pack_langue = findViewById(R.id.install_ivona_pack_tts);
		_launch_intent = findViewById(R.id.install_ivona_launch_intent);
	}

	private void ajouterListeners() {
		_install_ivona.setOnClickListener(this);
		_install_pack_langue.setOnClickListener(this);
		_launch_intent.setOnClickListener(this);
	}

	private void installInova() {
		Intent goToMarket = null;
		goToMarket = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://play.google.com/store/apps/details?id=com.ivona.tts&hl=fr"));
		startActivity(goToMarket);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.install_ivona_launch_tts:
			installInova();
			break;

		case R.id.install_ivona_pack_tts:
			installPackLangue();
			break;

		case R.id.install_ivona_launch_intent:
			launchIntentTTS();
			break;
		}
	}

	private void launchIntentTTS() {
		Intent intent = new Intent();
		intent.setAction("com.android.settings.TTS_SETTINGS");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	private void installPackLangue() {
		Intent goToMarket = null;
		goToMarket = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://mobile.ivona.com?ap=EMBED&v=1&set_lang=us"));
		startActivity(goToMarket);
	}

}
