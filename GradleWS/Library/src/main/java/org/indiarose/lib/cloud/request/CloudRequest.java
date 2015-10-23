package org.indiarose.lib.cloud.request;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.Hashtable;

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;

import android.text.TextUtils;
import android.util.Log;

public class CloudRequest
{
	protected static String FILENAME = "Filename";
	protected static String USER = "User";
	protected static String INDIAGRAM = "Indiagram";
	protected static String BLOBNAME = "Blobname";
	protected static String CATEGORY = "Category";
	protected static String PARENT = "Parent";
	protected static String CONTENT_LENGTH = "Content-length";
	
	protected Hashtable<String, String> m_headers = new Hashtable<String, String>();
	protected String m_method = "";
	protected String m_uri = "";
	
	protected String m_file = "";
	
	protected CloudRequest(String _method, String _uri)
	{
		this.m_method = _method;
		this.m_uri = _uri;
	}
	
	public void setFile(String _file)
	{
		this.m_file = _file;
	}
	
	public boolean hasFile()
	{
		return !TextUtils.isEmpty(this.m_file);
	}
	
	public String getFile()
	{
		return this.m_file;
	}
	
	public void addHeader(String _key, String _value)
	{
		this.m_headers.put(_key, _value);
	}
	
	public Hashtable<String, String> getHeaders()
	{
		return m_headers;
	}
	
	public String getMethod()
	{
		return m_method;
	}
	
	public String getUri()
	{
		return m_uri;
	}
	
	protected static String getName(Indiagram _indiagram)
	{
		return _indiagram.filePath.substring(0, _indiagram.filePath.length() - ".xml".length());
	}
	
	public static CloudRequest createIndiagramRequest(Indiagram _indiagram)
	{
		CloudRequest r = new CloudRequest("POST", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/indiaprivate");
		
		r.addHeader(USER, AppData.appData.login);
		r.addHeader(INDIAGRAM, getName(_indiagram));
		r.addHeader(CATEGORY, (_indiagram instanceof Category) ? "true" : "false");
		Category parent = Indiagram.getParent(_indiagram);
		r.addHeader(PARENT, (parent == null) ? "" : getName(parent));
		
		return r;
	}
	
	public static CloudRequest storeIndiagramPictureRequest(Indiagram _indiagram)
	{
		CloudRequest r = new CloudRequest("POST", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/picture");

		r.addHeader(USER, AppData.appData.login);
		r.addHeader(INDIAGRAM, getName(_indiagram));
		r.addHeader(FILENAME, _indiagram.imagePath);
		Log.wtf("CloudRequest", "storePicture : " + _indiagram.imagePath);
		r.setFile(PathData.IMAGE_DIRECTORY + _indiagram.imagePath);
		
		return r;
	}
	
	public static CloudRequest storeIndiagramXmlRequest(Indiagram _indiagram)
	{
		CloudRequest r = new CloudRequest("POST", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/xml");
		
		r.addHeader(USER, AppData.appData.login);
		r.addHeader(INDIAGRAM, getName(_indiagram));
		r.addHeader(FILENAME, _indiagram.filePath);
		r.setFile(PathData.XML_DIRECTORY + _indiagram.filePath);
		
		return r;
	}
	
	public static CloudRequest storeIndiagramSoundRequest(Indiagram _indiagram)
	{
		CloudRequest r = new CloudRequest("POST", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/sound");
		
		r.addHeader(USER, AppData.appData.login);
		r.addHeader(INDIAGRAM, getName(_indiagram));
		r.addHeader(FILENAME, _indiagram.soundPath);
		r.setFile(PathData.SOUND_DIRECTORY + _indiagram.soundPath);
		
		return r;
	}
	
	public static CloudRequest upVersionRequest()
	{
		CloudRequest r = new CloudRequest("PUT", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/upversion");
	
		r.addHeader(USER, AppData.appData.login);
			
		return r;
	}
	
	public static CloudRequest getIndiagramBlobNamesRequest(String _indiagramName)
	{
		CloudRequest r = new CloudRequest("GET", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/gindiaprivate/" + AppData.appData.login + "/" + _indiagramName);
		return r;
	}
	
	public static CloudRequest getIndiagramNamesRequest()
	{
		CloudRequest r = new CloudRequest("GET", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/listnameindiaprivate/" + AppData.appData.login);
		return r;
	}
	
	public static CloudRequest getVersionRequest()
	{
		CloudRequest r = new CloudRequest("GET", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/gversion/" + AppData.appData.login);
		return r;
	}
	
	public static CloudRequest deleteIndiagram(String _indiagramName)
	{
		CloudRequest r = new CloudRequest("DELETE", "http://indiaroserest.cloudapp.net:8080/ServiceIndia.svc/dindiaprivate");
		
		r.addHeader(USER, AppData.appData.login);
		r.addHeader(INDIAGRAM, _indiagramName);
		
		return r;
	}
	
	public static CloudRequest downloadBlobRequest(String _blobName)
	{
		CloudRequest r = new CloudRequest("GET", "https://indiarose.blob.core.windows.net/" + AppData.appData.login + "/" + _blobName);
		return r;
	}
}
