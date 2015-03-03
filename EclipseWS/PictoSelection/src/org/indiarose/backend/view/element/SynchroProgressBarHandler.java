package org.indiarose.backend.view.element;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import android.os.Handler;
import android.os.Message;

public class SynchroProgressBarHandler extends Handler
{
	public static final int PROGRESS_MESSAGE = 1;
	
	protected SynchroProgressBar m_object = null;
	
	public SynchroProgressBarHandler(SynchroProgressBar _object)
	{
		this.m_object = _object;
	}
	
	@Override
	public void handleMessage(Message _msg)
	{
		if(_msg.what == PROGRESS_MESSAGE)
		{
			this.m_object.progressASync(_msg.arg1, _msg.arg2);
		}
	}
}
