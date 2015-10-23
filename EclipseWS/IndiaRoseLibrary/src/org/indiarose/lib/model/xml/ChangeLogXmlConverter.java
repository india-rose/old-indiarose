package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.model.ChangeLog;

import storm.xmlbinder.XmlElement;
import storm.xmlbinder.binder.CollectionContentBinder;
import storm.xmlbinder.binder.ContentBinder;
import storm.xmlbinder.binder.NullBinder;
import storm.xmlbinder.binder.ObjectBinder;
import storm.xmlbinder.transformer.BooleanTransformer;
import storm.xmlbinder.transformer.StringTransformer;

public class ChangeLogXmlConverter extends AbstractXmlConverter<ChangeLog>
{
	/**
	 * Unique instance of the converter class to avoid useless duplicate data. 
	 */
	protected static ChangeLogXmlConverter s_instance = null;
	
	/**
	 * Construct the instance of the class and define all Xml binding rules.
	 */
	protected ChangeLogXmlConverter()
	{
		StringTransformer stringTransformer = new StringTransformer();
		BooleanTransformer booleanTransformer = new BooleanTransformer();
		
		XmlElement changeLog = new XmlElement("changelog", new ObjectBinder("", "org.indiarose.lib.model.ChangeLog"));
		XmlElement settings = new XmlElement("settings", new ContentBinder("settingsChanged", booleanTransformer));
		XmlElement add = new XmlElement("add", new NullBinder());
		XmlElement edit = new XmlElement("edit", new NullBinder());
		XmlElement del = new XmlElement("del", new NullBinder());
		
		XmlElement itemAdd = new XmlElement("item", new CollectionContentBinder("indiagramAdded", stringTransformer));
		XmlElement itemEdit = new XmlElement("item", new CollectionContentBinder("indiagramChanged", stringTransformer));
		XmlElement itemDel = new XmlElement("item", new CollectionContentBinder("indiagramDeleted", stringTransformer));
		
		add.addChild(itemAdd);
		edit.addChild(itemEdit);
		del.addChild(itemDel);
		
		changeLog.addChild(settings);
		changeLog.addChild(add);
		changeLog.addChild(edit);
		changeLog.addChild(del);
		
		this.m_rootElement = changeLog;
	}
	
	/**
	 * Retrieve the unique instance of the class.
	 * @return the instance of the CategoryXmlConverter class.
	 */
	public static ChangeLogXmlConverter getInstance()
	{
		if(ChangeLogXmlConverter.s_instance == null)
		{
			synchronized (ChangeLogXmlConverter.class)
			{
				if(ChangeLogXmlConverter.s_instance == null)
				{
					ChangeLogXmlConverter.s_instance = new ChangeLogXmlConverter();
				}
			}
		}
		return ChangeLogXmlConverter.s_instance;
	}
}
