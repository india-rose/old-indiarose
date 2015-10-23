package org.indiarose.indiarosetimer.utils;

import android.content.Context;

public class Metric {

	public static float pixelsToSp(Context context, Float px) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return px/scaledDensity;
	}
}
