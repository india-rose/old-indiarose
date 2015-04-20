package org.indiarose.lib.cloud.request;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.util.Log;

/**
 * Signal : 
 * 	- requestError(String e)
 * 	- requestSuccess(String content)
 * @author Julien
 *
 */
public class RequestExecuter extends Thread
{
	protected CloudRequest m_request = null;
	protected Object m_lock = new Object();
	
	public byte[] last = null;
	
	public RequestExecuter()
	{
	}
	
	public void setRequest(CloudRequest _request)
	{
		m_request = _request;
		synchronized(m_lock)
		{
			m_lock.notifyAll();
		}
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			while(m_request == null)
			{
				try
				{
					synchronized(m_lock)
					{
						m_lock.wait();
					}
				} 
				catch (InterruptedException e)
				{
					Log.wtf("RequestExecuter", "", e);
				}
			}
			Log.wtf("RequestExecuter", "Executing : " + m_request.getUri());
			HttpClient httpClient = new DefaultHttpClient();
			HttpUriRequest request = toHttpRequest();
			boolean ok = false;
			try
			{
				if(request.containsHeader("Content-Length"))
				{
					Log.wtf("Request content-length before", request.getHeaders("Content-Length")[0].getValue());
				}
				HttpResponse response = httpClient.execute(request);
				if(request.containsHeader("Content-Length"))
				{
					Log.wtf("Request content-length after", request.getHeaders("Content-Length")[0].getValue());
				}
				Log.wtf("RequestExecuter", "Got response : " + m_request.getUri());
				
				this.m_request = null;
				ok = true;
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					last = out.toByteArray();
					Log.wtf("RequestExecuter", out.toString());
					Mapper.emit(this, "requestSuccess", out.toString());
				}
				else
				{
					Log.wtf("RequestExecuter", "Error : " + response.getStatusLine().getStatusCode() + " # " + response.getStatusLine().getReasonPhrase());
					Log.wtf("ERROR", response.toString());
					Log.wtf("ERROR", "content length = " + response.getEntity().getContentLength());
					Header [] hs = response.getAllHeaders();
					for(Header h : hs)
					{
						Log.wtf("ERROR", "Name : " + h.getName() + " Value : " + h.getValue());
					}
					Header [] in = request.getAllHeaders();
					for(Header h : in)
					{
						Log.wtf("INIT", "Name : " + h.getName() + " Value : " + h.getValue());
					}
					HttpParams pa = request.getParams();
					Log.wtf("PARAM", pa.toString());
					
					response.getEntity().getContent().close();
					Mapper.emit(this, "requestError", response.getStatusLine().getReasonPhrase());
				}
			} 
			catch (Exception e)
			{
				Log.wtf("RequestExecuter", "exception while executing request", e);
				try
				{
					Mapper.emit(this, "requestError", e.getMessage());
				} 
				catch (MapperException e1)
				{
					Log.wtf("RequestExecuter", "Unable to emit requestError message", e1);
				}
			}
			finally
			{
				if(!ok)
				{
					this.m_request = null;
				}
			}
		}
	}
	
	protected HttpUriRequest toHttpRequest()
	{
		String method = m_request.getMethod();
		HttpUriRequest r = null;
		
		if(method.equals("GET"))
		{
			r = new HttpGet(m_request.getUri());
		}
		else if(method.equals("POST"))
		{
			r = new HttpPost(m_request.getUri());
		}
		else if(method.equals("DELETE"))
		{
			r = new HttpDelete(m_request.getUri());
		}
		else if(method.equals("PUT"))
		{
			r = new HttpPut(m_request.getUri());
		}
		
		Hashtable<String, String> headers = m_request.getHeaders();
		Enumeration<String> keys = headers.keys();
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			r.addHeader(key, headers.get(key));
		}
		
		if(m_request.hasFile())
		{
			InputStreamEntity reqEntity = null;
			try
			{
				File f = new File(m_request.getFile());
				
				reqEntity = new InputStreamEntity(new FileInputStream(f), f.length());
				reqEntity.setContentType("application/octet-stream");
			    reqEntity.setChunked(true); // Send in multiple parts if needed
			    ((HttpEntityEnclosingRequest) r).setEntity(reqEntity);
			    Log.wtf("Content-Length", "" + reqEntity.getContentLength());

			} 
			catch (FileNotFoundException e)
			{
				Log.wtf("Filenotfound", e);
			}
		    
		    /*
			
			File file = new File(m_request.getFile());
			FileEntity fileEntity = new FileEntity(file, "binary/octet-stream");
			((HttpEntityEnclosingRequest) r).setEntity(fileEntity);
			*/
		}
		
		return r;
	}
	
}
