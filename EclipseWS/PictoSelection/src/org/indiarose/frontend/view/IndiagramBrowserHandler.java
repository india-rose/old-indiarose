package org.indiarose.frontend.view;


/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import android.os.Handler;
import android.os.Message;

public class IndiagramBrowserHandler extends Handler
{
	public static final int RESET_MESSAGE = 1;
	public static final int NEXT_BUTTON_MESSAGE = 2;
	
	protected IndiagramBrowser m_object = null;
	
	public IndiagramBrowserHandler(IndiagramBrowser _object)
	{
		this.m_object = _object;
	}
	
	@Override
	public void handleMessage(Message _msg)
	{
		if(_msg.what == RESET_MESSAGE)
		{
			m_object.resetAsync();
		}
		else if(_msg.what == NEXT_BUTTON_MESSAGE)
		{
			m_object.refreshNextButtonAsync();
		}
	}
}
