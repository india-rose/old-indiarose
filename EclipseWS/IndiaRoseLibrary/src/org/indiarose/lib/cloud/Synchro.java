package org.indiarose.lib.cloud;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.util.Log;

/**
 * Class to execute synchronization between cloud and local.
 * Signal : 
 * 	- progress(int current, int max)
 * 	- finished()
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class Synchro
{
	protected IndiagramCreator m_creator = new IndiagramCreator();
	
	protected int m_current = 0;
	protected int m_total = 0;
	protected boolean m_isDownload = false;
	protected boolean m_isUpload = false;
	
	public Synchro()
	{
		try
		{
			Mapper.connect(m_creator, "created", this, "indiagramUploaded");
		} 
		catch (MapperException e)
		{
			Log.wtf("Synchro", "unable to connect creator", e);
		}
	}
	
	protected String getName(Indiagram _indiagram)
	{
		return _indiagram.filePath.substring(0, _indiagram.filePath.length() - ".xml".length());
	}
	
	public synchronized void indiagramUploaded(Indiagram _current)
	{
		if(m_isUpload)
		{
			AppData.changeLog.indiagramAdded.remove(getName(_current));
			m_current++;
			emit(m_current, m_total);
			if(m_current < m_total)
			{
				String addName = firstAdd();
				Indiagram indiagram = getIndiagram(addName + ".xml", AppData.homeCategory);
				m_creator.create(indiagram);
			}
			else
			{
				m_isUpload = false;
				try
				{
					Mapper.emit(this, "finished");
				} 
				catch (MapperException e)
				{
					Log.wtf("Synchro", "unable to emit progress signal", e);
				}
			}
		}
	}
	
	public void execute()
	{
		if(m_isUpload || m_isDownload)
			return;
		
		if(hasAdd())
		{
			m_current = 0;
			m_total = countAdd();
			m_isDownload = false;
			m_isUpload = true;
			emit(m_current, m_total);
			
			String addName = firstAdd();
			Indiagram indiagram = getIndiagram(addName + ".xml", AppData.homeCategory);
			
			m_creator.create(indiagram);
			
		}
		else
		{
			m_isDownload = true;
			IndiagramUpdater updater = new IndiagramUpdater();
			
			try
			{
				Mapper.connect(updater, "progress", this, "emit");
			}
			catch (MapperException e)
			{
				Log.wtf("Synchro", e);
			}
			
			updater.update();
		}
	}
	
	protected static Indiagram getIndiagram(String _name, Indiagram _root)
	{
		if(_root.filePath.equals(_name))
		{
			return _root;
		}
		if(_root instanceof Category)
		{
			Category c = (Category) _root;
			for(int i = 0 ; i < c.indiagrams.size() ; ++i)
			{
				Indiagram r = getIndiagram(_name, c.indiagrams.get(i));
				if(r != null)
				{
					return r;
				}
			}
		}
		return null;
	}
	
	protected int countAdd()
	{
		int cpt = 0;
		for(int i = 0 ; i < AppData.changeLog.indiagramAdded.size() ; ++i)
		{
			if(AppData.changeLog.indiagramAdded.get(i).startsWith("USER_"))
			{
				cpt++;
			}
		}
		return cpt;
	}
	
	protected String firstAdd()
	{
		for(int i = 0 ; i < AppData.changeLog.indiagramAdded.size() ; ++i)
		{
			if(AppData.changeLog.indiagramAdded.get(i).startsWith("USER_"))
			{
				return AppData.changeLog.indiagramAdded.get(i);
			}
		}
		return "";
	}
	
	protected boolean hasAdd()
	{
		for(int i = 0 ; i < AppData.changeLog.indiagramAdded.size() ; ++i)
		{
			if(AppData.changeLog.indiagramAdded.get(i).startsWith("USER_"))
			{
				return true;
			}
		}
		return false;
	}
	
	public void emit(Integer _current, Integer _max)
	{
		emit(_current.intValue(), _max.intValue());
		
		if(_current.intValue() == _max.intValue())
		{
			m_isDownload = false;
			try
			{
				Mapper.emit(this, "finished");
			} 
			catch (MapperException e)
			{
				Log.wtf("Synchro", "unable to emit end signal", e);
			}
		}
	}
	
	protected void emit(int _current, int _max)
	{
		try
		{
			Mapper.emit(this, "progress", _current, _max);
		} 
		catch (MapperException e)
		{
			Log.wtf("Synchro", "unable to emit progress signal", e);
		}
	}
}
