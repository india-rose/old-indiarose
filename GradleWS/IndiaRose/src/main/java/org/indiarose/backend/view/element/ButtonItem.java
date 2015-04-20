package org.indiarose.backend.view.element;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ButtonItem extends Button implements OnClickListener
{
	protected String m_identifier = "";
	
	public ButtonItem(Context context, String _identifier)
	{
		super(context);
		
		this.m_identifier = _identifier;
		this.setOnClickListener(this);
	}

	public String getIdentifier()
	{
		return m_identifier;
	}
	
	public void onClick(View v)
	{
		try
		{
			Mapper.emit(this, "clicked", this);
		} 
		catch (MapperException e)
		{
			Log.wtf("ButtonItem", "Mapper Emit problem", e);
		}
	}

}
