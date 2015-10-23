package org.indiarose.lib.model.xml.transformer;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import storm.xmlbinder.transformer.TransformerInterface;
import android.graphics.Color;

public class ColorTransformer implements TransformerInterface
{
	public Object read(String _value)
	{
		return Integer.valueOf(Color.parseColor(_value));
	}

	public String write(Object _value)
	{
		int value = ((Integer)_value).intValue();
		String res = "#" + toHex(Color.red(value)) + toHex(Color.green(value)) + toHex(Color.blue(value));
		return res;
	}
	
	protected String toHex(int _val)
	{
		String res = Integer.toHexString(_val);
		while(res.length() < 2)
		{
			res = "0" + res;
		}
		return res;
	}
}
