package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.xml.transformer.CategoryOrIndiagramPathTransformer;
import org.indiarose.lib.model.xml.transformer.CategoryPathTransformer;
import org.indiarose.lib.model.xml.transformer.ColorTransformer;
import org.indiarose.lib.model.xml.transformer.IndiagramPathTransformer;

import storm.xmlbinder.XmlElement;
import storm.xmlbinder.binder.CollectionContentBinder;
import storm.xmlbinder.binder.ContentBinder;
import storm.xmlbinder.binder.NullBinder;
import storm.xmlbinder.binder.ObjectBinder;
import storm.xmlbinder.transformer.StringTransformer;

/**
 * Class to convert org.indiarose.lib.model.Category to xml and vice-versa
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
final class CategoryXmlConverter extends AbstractXmlConverter<Category>
{
	/**
	 * Unique instance of the converter class to avoid useless duplicate data. 
	 */
	protected static CategoryXmlConverter s_instance = null;
	
	/**
	 * Construct the instance of the class and define all Xml binding rules.
	 */
	protected CategoryXmlConverter()
	{
		StringTransformer stringTransformer = new StringTransformer();
		CategoryOrIndiagramPathTransformer indiagramTransformer = new CategoryOrIndiagramPathTransformer();
		ColorTransformer colorTransformer = new ColorTransformer();
		
		XmlElement category = new XmlElement("category", new ObjectBinder("", "org.indiarose.lib.model.Category"));
		XmlElement text = new XmlElement("text", new ContentBinder("text", stringTransformer));
		XmlElement image = new XmlElement("picture", new ContentBinder("imagePath", stringTransformer));
		XmlElement sound = new XmlElement("sound", new ContentBinder("soundPath", stringTransformer));
		XmlElement textColor = new XmlElement("textcolor", new ContentBinder("textColor", colorTransformer));
		XmlElement indiagrams = new XmlElement("indiagrams", new NullBinder());
		XmlElement indiagram = new XmlElement("indiagram", new CollectionContentBinder("indiagrams", indiagramTransformer));
		
		indiagrams.addChild(indiagram);
		
		category.addChild(text);
		category.addChild(image);
		category.addChild(sound);
		category.addChild(textColor);
		category.addChild(indiagrams);
		
		this.m_rootElement = category;
	}
	
	/**
	 * Retrieve the unique instance of the class.
	 * @return the instance of the CategoryXmlConverter class.
	 */
	public static CategoryXmlConverter getInstance()
	{
		if(CategoryXmlConverter.s_instance == null)
		{
			synchronized (CategoryXmlConverter.class)
			{
				if(CategoryXmlConverter.s_instance == null)
				{
					CategoryXmlConverter.s_instance = new CategoryXmlConverter();
				}
			}
		}
		return CategoryXmlConverter.s_instance;
	}
	
	/**
	 * Read a category from the xml file _prefix + _filename
	 * @param _filename : the filename
	 * @param _prefix : the path prefix
	 * @return Category object
	 * @throws Exception
	 */
	public Category read(String _filename, String _prefix) throws Exception
	{
		CategoryPathTransformer.basePath = _prefix;
		IndiagramPathTransformer.basePath = _prefix;
		
		Category result = super.read(_prefix + _filename);
		result.filePath = _filename;
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.xml.AbstractXmlConverter#read(java.lang.String)
	 */
	@Override
	public Category read(String _filename) throws Exception
	{
		CategoryPathTransformer.basePath = PathData.XML_DIRECTORY;
		IndiagramPathTransformer.basePath = PathData.XML_DIRECTORY;
		
		Category result = super.read(PathData.XML_DIRECTORY + _filename);
		result.filePath = _filename;
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.xml.AbstractXmlConverter#write(java.lang.Object, java.lang.String)
	 */
	@Override
	public void write(Category _data, String _filename) throws Exception
	{
		CategoryPathTransformer.basePath = PathData.XML_DIRECTORY;
		IndiagramPathTransformer.basePath = PathData.XML_DIRECTORY;
		
		_data.filePath = _filename;
		super.write(_data, PathData.XML_DIRECTORY + _filename);
	}
	
	/**
	 * Write a category to xml file _prefix + _filename
	 * @param _data : the object to write into xml file.
	 * @param _filename : the name of the file.
	 * @param _prefix : the prefix where to write files.
	 * @throws Exception
	 */
	public void write(Category _data, String _filename, String _prefix) throws Exception
	{
		CategoryPathTransformer.basePath = _prefix;
		IndiagramPathTransformer.basePath = _prefix;
		
		_data.filePath = _filename;
		super.write(_data, _prefix + _filename);
	}
}
