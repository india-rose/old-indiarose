package org.indiarose.lib.utils;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageManager
{
	protected static Hashtable<String, Bitmap> m_images = new Hashtable<String, Bitmap>();
	
	public static Bitmap loadImage(String _imagePath, int _width, int _height) throws Exception
	{
		String key = _imagePath + "?" + _width + "?" + _height;
		if(!m_images.containsKey(key))
		{
			Bitmap image = null;

			try
			{
				 image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(_imagePath), _width, _height, true);
			}
			catch(Exception ex)
			{
				throw new Exception("ImageManager : Unable to load image " + _imagePath);
			}
			m_images.put(key, image);
		}
		
		return m_images.get(key);
	}
	
	public static Bitmap loadImage(String _imagePath) throws Exception
	{
		String key = _imagePath;
		if(!m_images.containsKey(key))
		{
			Bitmap image = null;
			try
			{
				 image = BitmapFactory.decodeFile(_imagePath);
			}
			catch(Exception ex)
			{
				throw new Exception("ImageManager : Unable to load image " + _imagePath);
			}
			
			m_images.put(key, image);
		}
		
		return m_images.get(key);
	}
}
