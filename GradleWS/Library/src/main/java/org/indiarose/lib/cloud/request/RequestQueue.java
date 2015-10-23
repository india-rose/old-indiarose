package org.indiarose.lib.cloud.request;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.concurrent.ConcurrentLinkedQueue;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.util.Log;

/**
 * Class to manage a queue of request.
 * 
 * Signal : 
 * 	- requestError(CloudRequest request, String error)
 * 	- requestFinished(CloudRequest request, String content)
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class RequestQueue
{
	protected ConcurrentLinkedQueue<CloudRequest> m_request = new ConcurrentLinkedQueue<CloudRequest>();
	protected RequestExecuter m_executer = null;
	protected boolean m_auto = true;
	
	public RequestQueue()
	{
		m_executer = new RequestExecuter();
		m_executer.start();
		
		try
		{
			Mapper.connect(m_executer, "requestError", this, "requestError");
			Mapper.connect(m_executer, "requestSuccess", this, "requestFinished");
		}
		catch(Exception e)
		{
			Log.wtf("RequestQueue", "unable to connect", e);
		}
	}
	
	public RequestQueue(boolean _autoLaunch)
	{
		this();
		m_auto = _autoLaunch;
	}
	
	public void start()
	{
		synchronized(this)
		{
			if(!m_auto && m_request.size() > 0)
			{
				next();
			}
		}
	}
	
	public void add(CloudRequest _request)
	{
		synchronized(this)
		{
			m_request.add(_request);
			if(m_auto && m_request.size() == 1)
			{
				next();
			}
		}
	}
	
	public void requestFinished(String _content)
	{
		CloudRequest r = processEndRequest();
		try
		{
			Mapper.emit(this, "requestFinished", r, _content);
		} 
		catch (MapperException e)
		{
			Log.wtf("RequestQueue", "unable to emit finish signal", e);
		}
	}
	
	public void requestError(String _error)
	{
		CloudRequest r = processEndRequest();
		try
		{
			Mapper.emit(this, "requestError", r, _error);
		} 
		catch (MapperException e)
		{
			Log.wtf("RequestQueue", "unable to emit error signal", e);
		}
	}
	
	protected void next()
	{
		CloudRequest r = m_request.peek();
		m_executer.setRequest(r);
	}
	
	protected CloudRequest processEndRequest()
	{
		synchronized(this)
		{
			CloudRequest r = m_request.poll();
			if(m_request.size() > 0)
			{
				next();
			}
			return r;
		}
	}
}
