package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Credits extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credits);
		
	}

	
	@Override
	public void onBackPressed() 
	{
		Intent i = new Intent(this, Home.class);
		startActivity(i);
		finish();
	}
}
