package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.model.InternalAppValue;

import storm.xmlbinder.XmlElement;
import storm.xmlbinder.binder.ContentBinder;
import storm.xmlbinder.binder.ObjectBinder;
import storm.xmlbinder.transformer.IntegerTransformer;
import storm.xmlbinder.transformer.StringTransformer;

/**
 * Class to bind xml file to InternalAppValue object.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public final class InternalAppValueXmlConverter extends AbstractXmlConverter<InternalAppValue>
{
	/**
	 * Unique instance of the converter class to avoid useless duplicate data. 
	 */
	protected static InternalAppValueXmlConverter s_instance = null;
	
	/**
	 * Construct the instance of the class and define all xml binding rules.
	 */
	protected InternalAppValueXmlConverter()
	{
		StringTransformer stringTransformer = new StringTransformer();
		IntegerTransformer intTransformer = new IntegerTransformer();
		
		XmlElement appdata = new XmlElement("appdata", new ObjectBinder("", "org.indiarose.lib.model.InternalAppValue"));
		XmlElement login = new XmlElement("login", new ContentBinder("login", stringTransformer));
		XmlElement password = new XmlElement("password", new ContentBinder("password", stringTransformer));
		XmlElement version = new XmlElement("version", new ContentBinder("version", intTransformer));
		
		appdata.addChild(login);
		appdata.addChild(password);
		appdata.addChild(version);
		
		this.m_rootElement = appdata;
	}
	
	/**
	 * Retrieve the unique instance of the class.
	 * @return the instance of the CategoryXmlConverter class.
	 */
	public static InternalAppValueXmlConverter getInstance()
	{
		if(InternalAppValueXmlConverter.s_instance == null)
		{
			synchronized (InternalAppValueXmlConverter.class)
			{
				if(InternalAppValueXmlConverter.s_instance == null)
				{
					InternalAppValueXmlConverter.s_instance = new InternalAppValueXmlConverter();
				}
			}
		}
		return InternalAppValueXmlConverter.s_instance;
	}
}
