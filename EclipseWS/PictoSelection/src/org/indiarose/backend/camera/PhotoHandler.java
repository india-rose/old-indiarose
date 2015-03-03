package org.indiarose.backend.camera;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.indiarose.lib.PathData;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;

/**
 * Signal : 
 * 	- pictureTaken(String _path) : when the photo is taken and saved.
 * @author Julien
 *
 */
public class PhotoHandler implements PictureCallback
{
	public void onPictureTaken(byte[] data, Camera camera)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "Picture_" + date + ".png";
		
		String filename = PathData.USER_IMAGE_DIRECTORY + photoFile;
		
		Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
		int [] colors = new int[300 * 300];
		int x = (b.getWidth() - 300) / 2;
		b.getPixels(colors, 0, 300, x, 0, 300, 300);
		Bitmap b2 = Bitmap.createBitmap(colors, 300, 300, b.getConfig());
		b.recycle();
		b = null;
		
		try 
		{
			FileOutputStream fos = new FileOutputStream(PathData.IMAGE_DIRECTORY + filename);
			b2.compress(Bitmap.CompressFormat.PNG, 0, fos);
		} 
		catch (Exception e) 
		{
			Log.wtf("PhotoHandler", "image can not be saved", e);
		}
		b2.recycle();
		b2 = null;
		try
		{
			Mapper.emit(this, "pictureTaken", filename);
		}
		catch (MapperException e)
		{
			Log.wtf("PhotoHandler", "Unable to emit signal", e);
		}
	}
	
	public static String onPictureTaken(Bitmap bitmap)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "Picture_" + date + ".png";
		
		String filename = PathData.USER_IMAGE_DIRECTORY + photoFile;
		
		try 
		{
			FileOutputStream fos = new FileOutputStream(PathData.IMAGE_DIRECTORY + filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
		} 
		catch (Exception e) 
		{
			Log.wtf("PhotoHandler", "image can not be saved", e);
		}
		//bitmap.recycle();
		//bitmap = null;
		
		return filename;
	}

}
