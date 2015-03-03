package org.indiarose.lib.cloud;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.cloud.request.CloudRequest;
import org.indiarose.lib.cloud.request.RequestQueue;
import org.indiarose.lib.model.Indiagram;

import storm.communication.Mapper;
import storm.communication.MapperException;

import android.text.TextUtils;
import android.util.Log;

/**
 * Class to manage indiagram creation on cloud.
 * 
 * Signal : 
 * 	- created(Indiagram current)
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class IndiagramCreator
{
	protected RequestQueue m_queue = new RequestQueue(false);
	protected Indiagram m_current = null;
	protected int m_numberOfRequest = 0;
	protected int m_numberOfFinishedRequest = 0;
	
	public IndiagramCreator()
	{
		try
		{
			Mapper.connect(m_queue, "requestFinished", this, "requestEnd");
			Mapper.connect(m_queue, "requestError", this, "requestError");
		} 
		catch (MapperException e)
		{
			Log.wtf("IndiagramCreator", "unable to connect queue", e);
		}
		
	}
	
	public synchronized void create(Indiagram _indiagram)
	{
		if(m_numberOfFinishedRequest != m_numberOfRequest)
		{
			Log.wtf("IndiagramCreator", "Currently creating an indiagram but try to create another one");
			return;
		}
		
		m_current = _indiagram;
		
		m_numberOfFinishedRequest = 0;
		m_numberOfRequest = 0;
		
		m_queue.add(CloudRequest.createIndiagramRequest(_indiagram));
		m_numberOfRequest++;
		m_queue.add(CloudRequest.storeIndiagramXmlRequest(_indiagram));
		m_numberOfRequest++;
		
		if(!TextUtils.isEmpty(_indiagram.imagePath))
		{
			m_queue.add(CloudRequest.storeIndiagramPictureRequest(_indiagram));
			m_numberOfRequest++;	
		}
		if(!TextUtils.isEmpty(_indiagram.soundPath))
		{
			m_queue.add(CloudRequest.storeIndiagramSoundRequest(_indiagram));
			m_numberOfRequest++;
		}
		m_queue.start();
	}
	
	public void requestError(CloudRequest _request, String _error)
	{
		Log.wtf("IndiagramCreator", "Request " + _request.getUri() + " error : " + _error);
		int n = 0;
		synchronized(this)
		{
			m_numberOfFinishedRequest++;
			n = m_numberOfFinishedRequest;
		}
		
		if(n == m_numberOfRequest)
		{
			try
			{
				Mapper.emit(this, "created", this.m_current);
			} 
			catch (MapperException e)
			{
				Log.wtf("IndiagramCreator", "unable to emit created signal", e);
			}
		}
	}
	
	public void requestEnd(CloudRequest _request, String _error)
	{
		int n = 0;
		synchronized(this)
		{
			m_numberOfFinishedRequest++;
			n = m_numberOfFinishedRequest;
		}
		
		if(n == m_numberOfRequest)
		{
			try
			{
				Mapper.emit(this, "created", this.m_current);
			} 
			catch (MapperException e)
			{
				Log.wtf("IndiagramCreator", "unable to emit created signal", e);
			}
		}
	}
}
