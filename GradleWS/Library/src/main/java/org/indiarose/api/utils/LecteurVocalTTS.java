package org.indiarose.api.utils;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

/**
 * This class provides an easy to use TTS speech synthetised
 * Insiantiate it with the current context, and read messages by lire(String) method
 * @author florentchampigny
 *
 */
public class LecteurVocalTTS implements TextToSpeech.OnInitListener{

	TextToSpeech _tts = null;
	boolean cree = false;
	
	Context context;

	public LecteurVocalTTS(Context context){
		this.context = context;

		try{
		_tts = new TextToSpeech(context,this);
		}catch(Exception e){}
		_tts.setSpeechRate(1);
		_tts.setPitch(1); 
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = _tts.setLanguage(Locale.getDefault());


			if (result > 0) 
				cree = true;
			else{
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				context.startActivity(installIntent); 
				((Activity)context).finish();
			}
				
		} else {
			Log.e("TTS", "Aucun module TextToSpeach n'est installe");
		}
	}

	public void lire(final String message){
		if(_tts != null){
			try{
				new Thread(new Runnable(){
					public void run(){
						while(!cree){
						}
						_tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
					}
				}).start();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void stop(){
		if(_tts != null)
			_tts.shutdown();
	}

}