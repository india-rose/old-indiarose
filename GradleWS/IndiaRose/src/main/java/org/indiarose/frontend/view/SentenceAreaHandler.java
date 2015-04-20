package org.indiarose.frontend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */


import android.os.Handler;
import android.os.Message;

public class SentenceAreaHandler extends Handler
{
	public static final int RESET_MESSAGE = 1;
	public static final int REFRESH_MESSAGE = 2;
	
	protected SentenceArea m_object = null;
	
	public SentenceAreaHandler(SentenceArea _object)
	{
		this.m_object = _object;
	}
	
	@Override
	public void handleMessage(Message _msg)
	{
		if(_msg.what == RESET_MESSAGE)
		{
			this.m_object.removeAllHandler();
		}
		else if(_msg.what == REFRESH_MESSAGE)
		{
			this.m_object.refreshLayout();
		}
	}
}
