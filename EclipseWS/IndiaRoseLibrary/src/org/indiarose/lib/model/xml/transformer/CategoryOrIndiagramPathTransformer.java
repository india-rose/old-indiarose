package org.indiarose.lib.model.xml.transformer;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.model.xml.CategoryOrIndiagramXmlConverter;

import storm.xmlbinder.transformer.TransformerInterface;

public class CategoryOrIndiagramPathTransformer implements TransformerInterface
{
	/*
	 * (non-Javadoc)
	 * @see storm.xmlbinder.transformer.TransformerInterface#read(java.lang.String)
	 */
	public Object read(String _value)
	{
		try
		{
			return CategoryOrIndiagramXmlConverter.getInstance().read(_value, CategoryPathTransformer.basePath);
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see storm.xmlbinder.transformer.TransformerInterface#write(java.lang.Object)
	 */
	public String write(Object _value)
	{
		return ((Indiagram)_value).filePath;
	}

}
