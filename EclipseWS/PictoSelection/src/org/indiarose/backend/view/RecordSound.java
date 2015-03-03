package org.indiarose.backend.view;


/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.view.element.AbstractScreen;
import org.indiarose.backend.view.element.ButtonItem;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Signal : 
 * 	- soundRecorded (String _filename)
 * @author Julien
 *
 */
public class RecordSound extends AbstractScreen
{
	protected static final String START = "start";
	protected static final String STOP = "stop";
	protected static final String CANCEL = "cancel";
	
	protected LinearLayout m_layout = null;
	
	protected String m_filePath = "";
	protected MediaRecorder m_recorder = null;
	
	protected ButtonItem buttonStart = null;
	protected ButtonItem buttonStop = null;
	protected ButtonItem buttonCancel = null;
	
	protected boolean m_isRecording = false;
	
	public RecordSound(ScreenManager _screen)
	{
		super(_screen);
		
		m_recorder = new MediaRecorder();
		
		buttonStart = new ButtonItem(AppData.currentContext, START);
		buttonStop = new ButtonItem(AppData.currentContext, STOP);
		buttonCancel = new ButtonItem(AppData.currentContext, CANCEL);
		
		ButtonItem [] views = new ButtonItem[]{buttonStart, buttonStop, buttonCancel};
		int [] resId = new int[]{R.string.startRecording, R.string.stopRecording, R.string.cancelText};
		
		m_layout = new LinearLayout(AppData.currentContext);
		m_layout.setOrientation(LinearLayout.HORIZONTAL);
		m_layout.setGravity(Gravity.CENTER);
		
		for(int i = 0 ; i < views.length ; ++i)
		{
			ButtonItem v = views[i];
			v.setTextColor(Color.BLACK);
			v.setTextSize(ScreenManager.fontSize);
			v.setText(resId[i]);
			m_layout.addView(v);
			try
			{
				Mapper.connect(v, "clicked", this, "buttonEvent");
			}
			catch(MapperException e)
			{
				Log.wtf("RecordSound", "unable to connect button", e);
			}
		}
		
		buttonStop.setVisibility(ButtonItem.GONE);
	}

	@Override
	public View getView()
	{
		return m_layout;
	}

	public synchronized void buttonEvent(ButtonItem _button)
	{
		String id = _button.getIdentifier();
		if(id.equals(CANCEL) && !m_isRecording)
		{
			this.m_screen.pop();
		}
		else if(id.equals(START) && !m_isRecording)
		{
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
		else if(id.equals(STOP))
		{
			m_recorder.stop();
			m_recorder.release();
			m_recorder = null;
			try
			{
				Mapper.emit(this, "soundRecorded", m_filePath);
			} 
			catch (MapperException e)
			{
				Log.wtf("RecordSound", "unable to emit signal soundRecorded", e);
			}
			this.m_screen.pop();
		}
	}
	
}
