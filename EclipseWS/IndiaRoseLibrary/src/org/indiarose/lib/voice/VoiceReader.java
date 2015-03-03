package org.indiarose.lib.voice;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.concurrent.LinkedBlockingQueue;

import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Indiagram;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.text.TextUtils;
import android.util.Log;

public class VoiceReader
{
	protected VoiceEngine m_engine = null;
	
	protected LinkedBlockingQueue<String> m_soundIds = new LinkedBlockingQueue<String>();
	protected LinkedBlockingQueue<Indiagram> m_indiagrams = new LinkedBlockingQueue<Indiagram>();
	
	public VoiceReader(VoiceEngine _engine)
	{
		this.m_engine = _engine;
		try
		{
			Mapper.connect(this.m_engine, "completed", this, "readingComplete");
		} 
		catch (MapperException e)
		{
			Log.wtf("VoiceReader", e);
		}
	}
	
	public VoiceEngine getEngine()
	{
		return this.m_engine;
	}
	
	public void read(Indiagram _indiagram)
	{
		String id = readIndiagram(_indiagram);
		Log.e("VoiceReader",id);
		if(id != null)
		{
			try
			{
				m_soundIds.put(id);
				m_indiagrams.put(_indiagram);
			} 
			catch (InterruptedException e)
			{
				Log.wtf("VoiceReader", e);
			}
		}
		else
		{
			try
			{
				Mapper.emit(this, "readingComplete", _indiagram);
			} 
			catch (MapperException e)
			{
				Log.wtf("VoiceReader", e);
			}
		}
	}
	
	public void readingComplete(String _id)
	{
		try
		{
			String lastSoundId = this.m_soundIds.take();
			if(_id.equals(lastSoundId))
			{
				try
				{
					Mapper.emit(this, "readingComplete", this.m_indiagrams.take());
				} 
				catch (MapperException e)
				{
					Log.wtf("VoiceReader", e);
				}
			}
			else
			{
				Log.wtf("VoiceReader", "Error with sound id, no corresponding");
			}
		}
		catch(Exception e)
		{
			Log.wtf("VoiceReader", e);
		}
	}
	
	protected String readIndiagram(Indiagram _indiagram)
	{
		if(!TextUtils.isEmpty(_indiagram.soundPath))
		{
			Log.e("VoiceReader",PathData.SOUND_DIRECTORY + _indiagram.soundPath);
			this.m_engine.addSound(PathData.SOUND_DIRECTORY + _indiagram.soundPath);
			return PathData.SOUND_DIRECTORY + _indiagram.soundPath;
		}
		//Log.e("SOUND", _indiagram.text);
		if(!TextUtils.isEmpty(_indiagram.text))
		{
			Log.e("VoiceReader",_indiagram.text);
			this.m_engine.addWord(_indiagram.text);
			return _indiagram.text;
		}
		return null;
	}
}
