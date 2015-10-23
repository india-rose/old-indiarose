package org.indiarose.lib.utils;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.Hashtable;

import android.graphics.Typeface;

public class FontManager
{
	protected static Hashtable<String, Typeface> m_fonts = new Hashtable<String, Typeface>();
	
	public static Typeface loadFont(String _fontPath)
	{
		if(!m_fonts.containsKey(_fontPath))
		{
			Typeface font = Typeface.createFromFile(_fontPath);
			if(font == null)
			{
				font = Typeface.DEFAULT;
			}
			m_fonts.put(_fontPath, font);
		}
		
		return m_fonts.get(_fontPath);
	}
}
