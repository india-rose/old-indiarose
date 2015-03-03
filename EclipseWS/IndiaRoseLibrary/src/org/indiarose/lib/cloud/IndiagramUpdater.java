package org.indiarose.lib.cloud;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.cloud.request.CloudRequest;
import org.indiarose.lib.cloud.request.RequestExecuter;
import org.indiarose.lib.cloud.response.IndiagramNames;
import org.indiarose.lib.cloud.response.IndiagramNamesResponseXmlConverter;
import org.indiarose.lib.model.Indiagram;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.text.TextUtils;
import android.util.Log;

/**
 * Signal : 
 * 	- progress(int _current, int _max)
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class IndiagramUpdater
{
	protected static final int WAITING_NAMES = 0;
	protected static final int WAITING_BLOB_INFO = 1;
	protected static final int WAITING_BLOB_CONTENT = 2;
	
	protected RequestExecuter m_executer = null;
	protected Indiagram m_currentIndiagram = null;
	protected int m_state = -1;
	protected ArrayList<String> m_indiagrams = null;
	protected int m_current = 0;
	protected String m_pictureName = "";
	
	
	public IndiagramUpdater()
	{
		m_executer = new RequestExecuter();
		m_executer.start();
		
		try
		{
			Mapper.connect(m_executer, "requestError", this, "error");
			Mapper.connect(m_executer, "requestSuccess", this, "end");
		} 
		catch (MapperException e)
		{
			Log.wtf("IndiagramUpdater", "42", e);
		}
		
	}
	
	public void update()
	{
		m_state = WAITING_NAMES;
		m_executer.setRequest(CloudRequest.getIndiagramNamesRequest());
	}
	
	public void end(String _content)
	{
		if(m_state == WAITING_NAMES)
		{
			m_indiagrams = new ArrayList<String>();
			m_current = 0;
			try
			{
				IndiagramNames names = new IndiagramNamesResponseXmlConverter().readFromContent(_content);
				for(int i = 0 ; i < names.indiagramNames.size() ; ++i)
				{
					if(names.indiagramNames.get(i).startsWith("USER_"))
					{
						m_indiagrams.add(names.indiagramNames.get(i));
					}
				}
			} 
			catch (Exception e)
			{
				Log.wtf("IndiagramUpdater", "unable to read names list response");
				return;
			}
			startIndiagramDownload();
		}
		else if(m_state == WAITING_BLOB_INFO)
		{
			try
			{
				IndiagramNames names = new IndiagramNamesResponseXmlConverter().readFromContent(_content);
				m_pictureName = "";
				for(int i = 0 ; i < names.indiagramNames.size() ; ++i)
				{
					if(names.indiagramNames.get(i).endsWith(".png"))
					{
						m_pictureName = names.indiagramNames.get(i);
						break;
					}
				}
				
				if(TextUtils.isEmpty(m_pictureName))
				{
					startIndiagramDownload();
				}
				else
				{
					m_state = WAITING_BLOB_CONTENT;
					m_executer.setRequest(CloudRequest.downloadBlobRequest(m_pictureName));
				}
			} 
			catch (Exception e)
			{
				Log.wtf("IndiagramUpdater", "unable to read blob names list response");
				return;
			}
		}
		else if(m_state == WAITING_BLOB_CONTENT)
		{
			m_currentIndiagram.imagePath = m_pictureName;
			
			try
			{
				FileOutputStream out = new FileOutputStream(new File(PathData.IMAGE_DIRECTORY + m_pictureName));
				out.write(m_executer.last);
				out.close();
				AppData.Save(m_currentIndiagram);
			} catch (Exception e)
			{
				Log.wtf("IndiagramUpdater", "IOEXcetpion", e);
			}
			
			
			startIndiagramDownload();
		}
	}

	protected void startIndiagramDownload()
	{
		try
		{
			Mapper.emit(this, "progress", m_current, m_indiagrams.size());
		} catch (MapperException e)
		{
			Log.wtf("IndiagramUpdater", "hello world");
		}
		if(m_current < m_indiagrams.size())
		{
			String name = m_indiagrams.get(m_current);
			m_currentIndiagram = Synchro.getIndiagram(name + ".xml", AppData.homeCategory);
			if(m_currentIndiagram == null)
			{
				Log.wtf("IndiagramUpdater", "unable to find indiagram with name " + name);
				m_current++;
				startIndiagramDownload();
			}
			else
			{
				m_state = WAITING_BLOB_INFO;
				m_executer.setRequest(CloudRequest.getIndiagramBlobNamesRequest(name));
				m_current++;
			}
		}
	}
	
	public void error(String _e)
	{
		try
		{
			Mapper.emit(this, "progress", 1, 1);
		} 
		catch (MapperException e)
		{
			Log.wtf("IndiagramUpdater", "bouh", e);
		}
	}
}
