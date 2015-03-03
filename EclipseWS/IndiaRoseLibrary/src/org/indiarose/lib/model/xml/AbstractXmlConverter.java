package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import android.util.Log;
import storm.xmlbinder.XmlBinderException;
import storm.xmlbinder.XmlElement;
import storm.xmlbinder.XmlReader;
import storm.xmlbinder.XmlWriter;

/**
 * Base class for all xml converter.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 * @param <T> : the type of the object used to read and write xml file. This must be specified in the extends place.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractXmlConverter<T>
{
	/**
	 * The root xml binder rules element.
	 */
	protected XmlElement m_rootElement = null;
	
	/**
	 * Method to read a file and convert it to the object.
	 * @param _filename : the name of the file to read.
	 * @return the created object.
	 */
	public T read(String _filename) throws Exception
	{
		try
		{
			XmlReader reader = new XmlReader(this.m_rootElement);
			return (T)reader.read(_filename);
		}
		catch(XmlBinderException ex)
		{
			throw new Exception(this.getClass().toString() + " : error while reading file " + _filename);
		}
	}
	/**
	 * Method to read a xml content from a string and convert it to the object.
	 * @param _content : the xml content.
	 * @return the created object.
	 */
	public T readFromContent(String _content) throws Exception
	{
		try
		{
			XmlReader reader = new XmlReader(this.m_rootElement);
			return (T)reader.readFromContent(_content);
		}
		catch(XmlBinderException ex)
		{
			throw new Exception(this.getClass().toString() + " : error while reading content " + _content);
		}
	}
	
	/**
	 * Method to write an object to a xml file.
	 * @param _data : the object to write.
	 * @param _filename : the name of the xml file.
	 */
	public void write(T _data, String _filename) throws Exception
	{
		try
		{
			XmlWriter writer = new XmlWriter(this.m_rootElement);
			writer.write(_data, _filename);
		}
		catch(XmlBinderException ex)
		{
			Log.wtf("AbstractXmlConverter", "error", ex);
			throw new Exception(this.getClass().toString() + " : error while writing file " + _filename);
		}
	}
	/**
	 * Method to write an object to a xml content.
	 * @param _data : the object to write.
	 * @return the xml generated content.
	 */
	public String writeToContent(T _data) throws Exception
	{
		try
		{
			XmlWriter writer = new XmlWriter(this.m_rootElement);
			return writer.getXml(_data);
		}
		catch(XmlBinderException ex)
		{
			throw new Exception(this.getClass().toString() + " : error while writing xml content"); 
		}
	}
	
	/**
	 * Method to get the xml rules root element to bind the element.
	 * @return the xml root rules element.
	 */
	public XmlElement getXmlRules()
	{
		return this.m_rootElement;
	}
}
