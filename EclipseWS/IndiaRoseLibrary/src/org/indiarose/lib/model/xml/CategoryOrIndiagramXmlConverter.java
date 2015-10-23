package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;

import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.model.xml.transformer.CategoryPathTransformer;
import org.indiarose.lib.model.xml.transformer.IndiagramPathTransformer;

import android.util.Log;

import storm.xmlbinder.XmlBinderException;
import storm.xmlbinder.XmlElement;
import storm.xmlbinder.XmlReader;
import storm.xmlbinder.XmlWriter;

public final class CategoryOrIndiagramXmlConverter extends AbstractXmlConverter<Indiagram>
{
	/**
	 * Unique instance of the converter class to avoid useless duplicate data. 
	 */
	protected static CategoryOrIndiagramXmlConverter s_instance = null;
	
	/**
	 * Construct the instance of the class and define all Xml binding rules.
	 */
	protected CategoryOrIndiagramXmlConverter()
	{		
		this.m_rootElement = null;
	}
	
	/**
	 * Retrieve the unique instance of the class.
	 * @return the instance of the CategoryXmlConverter class.
	 */
	public static CategoryOrIndiagramXmlConverter getInstance()
	{
		if(CategoryOrIndiagramXmlConverter.s_instance == null)
		{
			synchronized (CategoryOrIndiagramXmlConverter.class)
			{
				if(CategoryOrIndiagramXmlConverter.s_instance == null)
				{
					CategoryOrIndiagramXmlConverter.s_instance = new CategoryOrIndiagramXmlConverter();
				}
			}
		}
		return CategoryOrIndiagramXmlConverter.s_instance;
	}
	
	/**
	 * Read a category from the xml file _prefix + _filename
	 * @param _filename : the filename
	 * @param _prefix : the path prefix
	 * @return Category object
	 * @throws Exception
	 */
	public Indiagram read(String _filename, String _prefix) throws Exception
	{		
		CategoryPathTransformer.basePath = _prefix;
		IndiagramPathTransformer.basePath = _prefix;
		
		try
		{
			ArrayList<XmlElement> elements = new ArrayList<XmlElement>();
			elements.add(CategoryXmlConverter.getInstance().getXmlRules());
			elements.add(IndiagramXmlConverter.getInstance().getXmlRules());
			XmlReader reader = new XmlReader(elements);
			Indiagram result = (Indiagram)reader.read(_prefix + _filename);
			result.filePath = _filename;
			return result;
		}
		catch(XmlBinderException ex)
		{
			Log.wtf("CategoryIndiagramXml", ex);
			throw new Exception(this.getClass().toString() + " : error while reading file " + _filename);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.xml.AbstractXmlConverter#read(java.lang.String)
	 */
	@Override
	public Indiagram read(String _filename) throws Exception
	{
		return this.read(_filename, PathData.XML_DIRECTORY);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.xml.AbstractXmlConverter#write(java.lang.Object, java.lang.String)
	 */
	@Override
	public void write(Indiagram _data, String _filename) throws Exception
	{
		this.write(_data, _filename, PathData.XML_DIRECTORY);
	}
	
	/**
	 * Write a category to xml file _prefix + _filename
	 * @param _data : the object to write into xml file.
	 * @param _filename : the name of the file.
	 * @param _prefix : the prefix where to write files.
	 * @throws Exception
	 */
	public void write(Indiagram _data, String _filename, String _prefix) throws Exception
	{
		CategoryPathTransformer.basePath = _prefix;
		IndiagramPathTransformer.basePath = _prefix;
		
		_data.filePath = _filename;
		try
		{
			XmlElement element = null;
			if(_data instanceof Category)
			{
				element = CategoryXmlConverter.getInstance().getXmlRules();
			}
			else
			{
				element = IndiagramXmlConverter.getInstance().getXmlRules();
			}
			XmlWriter writer = new XmlWriter(element);
			writer.write(_data, _prefix + _filename);
		}
		catch(XmlBinderException ex)
		{
			throw new Exception(this.getClass().toString() + " : error while writing file " + _filename);
		}
	}
}
