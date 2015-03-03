package org.indiarose.backend.alert;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import org.indiarose.R;
import org.indiarose.backend.activity.AddIndiagram;
import org.indiarose.backend.view.element.ButtonItem;
import org.indiarose.lib.PathData;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Signal : 
 * 	- soundRecorded (String _filename)
 * @author Julien
 *
 */
public class RecordSound extends AlertDialog implements View.OnClickListener
{

	protected Context AddIndriagram;
	
	public RecordSound(Context context) {
		super(context);
		AddIndriagram = context;
	}

	protected View buttonStart = null;
	protected View buttonStop = null;
	protected View buttonCancel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alertdialog_record_sound);
		m_recorder = new MediaRecorder();
		ajouterVues();
		ajouterListeners();
		
	}

	protected void ajouterVues(){
		buttonStart = findViewById(R.id.alertdialog_record_sound_start);
		buttonStop = findViewById(R.id.alertdialog_record_sound_stop);
		buttonCancel = findViewById(R.id.alertdialog_record_sound_cancel);
	}

	protected void ajouterListeners(){
		buttonStart.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	public String m_filePath = "";
	protected MediaRecorder m_recorder = null;

	protected boolean m_isRecording = false;

	public void onClick(View v){
		switch(v.getId()){
		case R.id.alertdialog_record_sound_start:
			record();
			break;
		case R.id.alertdialog_record_sound_stop:
			stopRecord();
			this.dismiss();
			break;
		case R.id.alertdialog_record_sound_cancel:
			this.dismiss();
			break;
		}
	}

	protected void record(){
		m_isRecording = true;
		buttonStart.setVisibility(ButtonItem.GONE);
		buttonCancel.setVisibility(ButtonItem.GONE);
		buttonStop.setVisibility(ButtonItem.VISIBLE);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		m_filePath = PathData.USER_SOUND_DIRECTORY + "Record_" + date + ".3gp";

		m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		m_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		m_recorder.setOutputFile(PathData.SOUND_DIRECTORY + m_filePath);
		try
		{
			m_recorder.prepare();
		}
		catch(Exception e)
		{
			Log.wtf("RecordSound", "unable to prepare recorder", e);
		}
		m_recorder.start();
	}

	protected void stopRecord(){
		m_recorder.stop();
		m_recorder.release();
		m_recorder = null;
		((AddIndiagram) AddIndriagram).saveSound(m_filePath);
		((AddIndiagram) AddIndriagram).onPush();
	}

}
