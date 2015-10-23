package org.indiarose.backend;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */


import org.indiarose.R;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TitleBar
{
	protected RelativeLayout m_layout = null;
	
	public TitleBar(RelativeLayout _layout)
	{
		ImageView logo = new ImageView(_layout.getContext());
		logo.setId(0x0fffff2a);
		logo.setImageResource(R.drawable.logo);
		logo.setAdjustViewBounds(true);
		
		_layout.setBackgroundColor(Color.WHITE);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.setMargins(0, 40, 20, 20);
		_layout.addView(logo, lp);
	}
}