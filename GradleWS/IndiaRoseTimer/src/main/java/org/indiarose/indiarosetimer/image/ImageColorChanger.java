package org.indiarose.indiarosetimer.image;

import android.graphics.Bitmap;

public class ImageColorChanger {

	public static Bitmap changerCouleurBitmap(Bitmap bitmap, int couleurAChanger, int nouvelleCouleur){
		int [] allpixels = new int [ bitmap.getHeight()*bitmap.getWidth()];
		Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);

		bitmap.getPixels(allpixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),bitmap.getHeight());
		bitmap.recycle();
		System.gc();

		for(int i =0; i<bitmap.getHeight()*bitmap.getWidth();i++){

			if( allpixels[i] == couleurAChanger)
				allpixels[i] = nouvelleCouleur;
		}

		newBitmap.setPixels(allpixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		return newBitmap;
	}
}
