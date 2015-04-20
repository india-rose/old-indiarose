package org.indiarose.indiarosetimebar.utils;

import android.content.Context;
import android.util.TypedValue;

public class DisplayConverter {

	public static float spToPixels(Context context, Float sp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
	}
	
	public static float dipToPixels(Context context, Float dip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
	}
	
}
