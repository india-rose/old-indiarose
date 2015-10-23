package org.indiarose.lib.cloud.response;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.model.xml.AbstractXmlConverter;

import storm.xmlbinder.XmlElement;
import storm.xmlbinder.binder.CollectionContentBinder;
import storm.xmlbinder.binder.ObjectBinder;
import storm.xmlbinder.transformer.StringTransformer;

public class IndiagramNamesResponseXmlConverter extends AbstractXmlConverter<IndiagramNames>
{
	/**
	 * Construct the instance of the class and define all Xml binding rules.
	 */
	public IndiagramNamesResponseXmlConverter()
	{
		StringTransformer stringTransformer = new StringTransformer();
		
		XmlElement root = new XmlElement("ArrayOfstring", new ObjectBinder("", "org.indiarose.lib.cloud.response.IndiagramNames"));
		XmlElement item = new XmlElement("string", new CollectionContentBinder("indiagramNames", stringTransformer));
		
		root.addChild(item);
		
		this.m_rootElement = root;
	}
}
