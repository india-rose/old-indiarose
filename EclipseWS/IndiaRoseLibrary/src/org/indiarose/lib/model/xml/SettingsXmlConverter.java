package org.indiarose.lib.model.xml;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.model.Settings;
import org.indiarose.lib.model.xml.transformer.ColorTransformer;

import storm.xmlbinder.XmlElement;
import storm.xmlbinder.binder.ContentBinder;
import storm.xmlbinder.binder.ObjectBinder;
import storm.xmlbinder.transformer.BooleanTransformer;
import storm.xmlbinder.transformer.FloatTransformer;
import storm.xmlbinder.transformer.IntegerTransformer;
import storm.xmlbinder.transformer.StringTransformer;

/**
 * Converter class to turn Settings object into Xml file and vice-versa.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public final class SettingsXmlConverter extends AbstractXmlConverter<Settings>
{
	/**
	 * The unique instance of the class.
	 */
	protected static SettingsXmlConverter s_instance = null;
	
	protected SettingsXmlConverter()
	{
		StringTransformer stringTransformer = new StringTransformer();
		IntegerTransformer intTransformer = new IntegerTransformer();
		BooleanTransformer booleanTransformer = new BooleanTransformer();
		FloatTransformer floatTransformer = new FloatTransformer();
		ColorTransformer colorTransformer = new ColorTransformer();
		
		XmlElement settings = new XmlElement("settings", new ObjectBinder("", "org.indiarose.lib.model.Settings"));
		
		settings.addChild(new XmlElement("heightSelectionArea", new ContentBinder("heightSelectionArea", intTransformer)));
		
		settings.addChild(new XmlElement("backgroundSelectionArea", new ContentBinder("backgroundSelectionArea", colorTransformer)));
		settings.addChild(new XmlElement("backgroundReinforcerReading", new ContentBinder("backgroundReinforcerReading", colorTransformer)));
		settings.addChild(new XmlElement("backgroundSentenceArea", new ContentBinder("backgroundSentenceArea", colorTransformer)));
		
		settings.addChild(new XmlElement("languageIsoCode", new ContentBinder("languageIsoCode", stringTransformer)));
		settings.addChild(new XmlElement("indiagramSize", new ContentBinder("indiagramSize", intTransformer)));
		
		settings.addChild(new XmlElement("fontFamily", new ContentBinder("fontFamily", stringTransformer)));
		settings.addChild(new XmlElement("fontSize", new ContentBinder("fontSize", intTransformer)));
		
		settings.addChild(new XmlElement("wordsReadingDelay", new ContentBinder("wordsReadingDelay", floatTransformer)));
		settings.addChild(new XmlElement("enableDragAndDrop", new ContentBinder("enableDragAndDrop", booleanTransformer)));
		settings.addChild(new XmlElement("enableCategoryReading", new ContentBinder("enableCategoryReading", booleanTransformer)));
		settings.addChild(new XmlElement("enableReadingReinforcer", new ContentBinder("enableReadingReinforcer", booleanTransformer)));
		settings.addChild(new XmlElement("enableSelectionReinforcer", new ContentBinder("enableSelectionReinforcer", booleanTransformer)));
		
		this.m_rootElement = settings;
	}
	
	/**
	 * Method to get the instance of the class.
	 * @return the unique instance of the class.
	 */
	public static SettingsXmlConverter getInstance()
	{
		if(SettingsXmlConverter.s_instance == null)
		{
			synchronized (SettingsXmlConverter.class)
			{
				if(SettingsXmlConverter.s_instance == null)
				{
					SettingsXmlConverter.s_instance = new SettingsXmlConverter();
				}
			}
		}
		return SettingsXmlConverter.s_instance;
	}
}
