package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.model.xml.transformer.CategoryPathTransformer;
import org.indiarose.lib.model.xml.transformer.IndiagramPathTransformer;

import storm.xmlbinder.XmlElement;
import storm.xmlbinder.binder.ContentBinder;
import storm.xmlbinder.binder.ObjectBinder;
import storm.xmlbinder.transformer.StringTransformer;

/**
 * This class convert org.indiarose.lib.model.Indiagram to Xml and vice-versa
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
final class IndiagramXmlConverter extends AbstractXmlConverter<Indiagram>
{
	/**
	 * The unique instance of the IndiagramXmlConverter
	 */
	protected static IndiagramXmlConverter s_instance = null;
	
	/**
	 * Constructor of the class, initialize xml rules.
	 */
	protected IndiagramXmlConverter()
	{
		StringTransformer stringTransformer = new StringTransformer();
		XmlElement indiagram = new XmlElement("indiagram", new ObjectBinder("", "org.indiarose.lib.model.Indiagram"));
		XmlElement text = new XmlElement("text", new ContentBinder("text", stringTransformer));
		XmlElement image = new XmlElement("picture", new ContentBinder("imagePath", stringTransformer));
		XmlElement sound = new XmlElement("sound", new ContentBinder("soundPath", stringTransformer));
		
		indiagram.addChild(text);
		indiagram.addChild(image);
		indiagram.addChild(sound);
		
		this.m_rootElement = indiagram;
	}
	
	/**
	 * Retrieve the unique instance of the class.
	 * @return the instance of the IndiagramXmlConverter class.
	 */
	public static IndiagramXmlConverter getInstance()
	{
		if(IndiagramXmlConverter.s_instance == null)
		{
			synchronized (IndiagramXmlConverter.class)
			{
				if(IndiagramXmlConverter.s_instance == null)
				{
					IndiagramXmlConverter.s_instance = new IndiagramXmlConverter();
				}
			}
		}
		return IndiagramXmlConverter.s_instance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.xml.AbstractXmlConverter#read(java.lang.String)
	 */
	@Override
	public Indiagram read(String _filename) throws Exception
	{
		CategoryPathTransformer.basePath = PathData.XML_DIRECTORY;
		IndiagramPathTransformer.basePath = PathData.XML_DIRECTORY;
		
		Indiagram result = super.read(_filename);
		result.filePath = _filename;
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.xml.AbstractXmlConverter#write(java.lang.Object, java.lang.String)
	 */
	@Override
	public void write(Indiagram _data, String _filename) throws Exception
	{
		_data.filePath = _filename;
		super.write(_data, _filename);
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
		super.write(_data, _prefix + _filename);
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
		
		Indiagram result = super.read(_prefix + _filename);
		result.filePath = _filename;
		return result;
	}
}
