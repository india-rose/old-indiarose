package org.indiarose;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */


import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import org.indiarose.R;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "equipe.indiarose@gmail.com",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		ACRA.init(this);
	}
}
