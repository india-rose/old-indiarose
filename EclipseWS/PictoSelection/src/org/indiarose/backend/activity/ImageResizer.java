package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

public class ImageResizer {

	
	public static Bitmap resizeExtra(Activity activity, int w, int h) {
		Uri imageUri = (Uri) activity.getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {

			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				InputStream is = null;
				
				is = activity.getContentResolver().openInputStream(imageUri); 
				BitmapFactory.decodeStream(is,null,options);
				is.close();
				is = activity.getContentResolver().openInputStream(imageUri); 
				options.inSampleSize = Math.max(options.outWidth/w, options.outHeight/h);
				Bitmap bitmap = BitmapFactory.decodeStream(is,null,options);
				is.close();

				return bitmap;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Bitmap scaleImage(Bitmap bitmap, int boundBoxInDp){
		// Get current dimensions
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundBoxInDp) / width;
		float yScale = ((float) boundBoxInDp) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		// Create a new bitmap and convert it to a format understood by the ImageView
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return scaledBitmap;
	}
	
	public static Bitmap applyOrientation(Bitmap bitmap,File bitmapFile) {
		try{
			
			System.gc();
			
			ExifInterface exif = null;
			exif = new ExifInterface(bitmapFile.getAbsolutePath());

			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			int rotate = 0;
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			default:
				return bitmap;
			}

			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix mtx = new Matrix();
			mtx.postRotate(rotate);
			Bitmap  bm_rotated = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
			
			//flip horizontal
			//Matrix mt2 = new Matrix();
			//mt2.preScale(-1, 1);
		    //Bitmap dst = Bitmap.createBitmap(bm_rotated, 0, 0, bm_rotated.getWidth(), bm_rotated.getHeight(), mt2, false);
		    //dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		    
		    return bm_rotated;
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}
	
	
}
