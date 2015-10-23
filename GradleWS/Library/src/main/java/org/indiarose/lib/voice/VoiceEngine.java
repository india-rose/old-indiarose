package org.indiarose.lib.voice;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;
import android.util.Log;

public class VoiceEngine implements OnInitListener {
	protected final int CHECKING_CODE = 1515;

	protected Activity m_context = null;
	protected TextToSpeech m_tts = null;
	protected Locale m_language = Locale.FRENCH;

	protected VoiceThreadStateManager m_stateManager = null;
	protected LinkedBlockingQueue<String> m_soundIds = new LinkedBlockingQueue<String>();

	boolean initialized = false;

	public VoiceEngine(Activity _context, Locale _language) {
		Log.e("VoiceEngine", "Je creer le voice engine");
		this.m_context = _context;
		this.m_language = _language;
		this.m_stateManager = new VoiceThreadStateManager(this);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 17) {
			Log.e("VoiceEngine", currentapiVersion + "");
			this.m_tts = new TextToSpeech(this.m_context, this);
			Log.e("VoiceEngine", "tts != null" + (m_tts != null));
			this.m_stateManager.start();
		} else {
			Log.e("VoiceEngine", "Version" + currentapiVersion);
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			this.m_context.startActivityForResult(checkIntent, CHECKING_CODE);
		}
	}

	@SuppressWarnings("unused")
	private void installVoiceData() {
		Intent intent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setPackage("com.google.android.tts"/*
												 * replace with the package name
												 * of the target TTS engine
												 */);
		try {
			Log.v("VOICE", "Installing voice data: " + intent.toUri(0));
			this.m_context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.e("VOICE", "Failed to install TTS data, no acitivty found for "
					+ intent + ")");
		}
	}

	public void onInit(int _status) {
		if (!initialized) {
			initialized = true;
			// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
			if (_status == TextToSpeech.SUCCESS) {
				// Set preferred language to the language in attribute.

				try {
					int result = this.m_tts.isLanguageAvailable(m_language);
					if (result >= 0) {
						this.m_tts.setLanguage(this.m_language);
					} else {
						this.m_language = Locale.ENGLISH;
						if (this.m_tts.isLanguageAvailable(m_language) == TextToSpeech.LANG_AVAILABLE) {
							this.m_tts.setLanguage(this.m_language);
						} else {
							destroy();
							Log.wtf("VoiceEngine",
									"Unable to find language for voice engine");
						}
					}
				} catch (Exception e) {
					destroy();
					Log.wtf("VoiceEngine", "Erreur TTS");
				}
				// initialisezd = true;
			} else if (_status == TextToSpeech.ERROR) {
				destroy();
				Log.wtf("VoiceEngine",
						"Error occurred while initializing engine");
			}
		}

	}

	public void destroy() {
		if (this.m_tts != null) {
			if (initialized) {
				this.m_tts.stop();
				this.m_tts.shutdown();
			}
			this.m_tts = null;

		}
	}

	public void activityResult(int _requestCode, int _resultCode, Intent _data) {
		Log.e("VoicieEngine", "activityResult");
		if (!initialized) {
			if (_requestCode == CHECKING_CODE) {
				Log.wtf("Bouh", "VoiceEngine present");

				if (_resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					Log.wtf("Bouh", "VoiceEngine pass");

					// success, create the TTS instance
					this.m_tts = new TextToSpeech(this.m_context, this);

					this.m_stateManager.start();
				} else {
					Log.wtf("VoiceEngine",
							"Missing data for textToSpeech no auto install");
					// TODO Can install data
					Intent installIntent = new Intent();
					installIntent
							.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
					this.m_context.startActivity(installIntent);

				}
			} else {
				Log.wtf("Bouh", "VoiceEngine missing");
			}
		}
	}

	public boolean isReading() {
		if (this.m_tts != null) {
			return this.m_tts.isSpeaking();
		}
		return false;
	}

	public void addWord(String _word) {
		Log.e("Voice Engine", "null ?" + (this.m_tts != null));
		Log.e("Voice Engine", "null ?" + (!TextUtils.isEmpty(_word)));
		if (this.m_tts != null && !TextUtils.isEmpty(_word)) {
			try {
				Log.e("SOUND2", _word);
				m_soundIds.put(_word);
				// this.m_tts.speak(_word, TextToSpeech.QUEUE_ADD, null);
				this.m_tts.speak(_word, TextToSpeech.QUEUE_ADD, null);
			} catch (Exception ex) {
				Log.e("SOUND_ERR", ex.toString());
			}
		}
	}

	public void addSound(String _filename) {
		if (this.m_tts != null && !TextUtils.isEmpty(_filename)) {
			try {
				Log.e("VoiceEngine", "addSound" + _filename);

				m_soundIds.put(_filename);

				this.m_tts.addSpeech(_filename, _filename);
				this.m_tts.speak(_filename, TextToSpeech.QUEUE_ADD, null);
			} catch (Exception ex) {
				Log.wtf("VoiceEngine", "addSound problem", ex);
			}
		}
	}

	public void endReading() {
		try {
			String soundId = m_soundIds.take();

			try {
				Mapper.emit(this, "completed", soundId);
			} catch (MapperException e) {
				Log.wtf("VoiceEngine", "While reading : " + soundId, e);
			}
		} catch (Exception ex) {
			Log.wtf("VoiceEngine", ex);
		}
	}
}
