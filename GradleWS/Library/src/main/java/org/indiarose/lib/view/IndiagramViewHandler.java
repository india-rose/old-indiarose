package org.indiarose.lib.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import android.os.Handler;
import android.os.Message;

/**
 * This handler has to manage all change to IndiagramView.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class IndiagramViewHandler extends Handler
{
	/**
	 * Message to invalidate the view and force redraw.
	 */
	public static final int REFRESH_VIEW = 1;
	
	/**
	 * The related view of this handler.
	 */
	protected IndiagramView m_view = null;
	
	/**
	 * Construct a new IndiagramViewHandler and attach it to a IndiagramView.
	 * @param _view : the IndiagramView attached to this.
	 */
	public IndiagramViewHandler(IndiagramView _view)
	{
		this.m_view = _view;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message _msg) 
	{
		if(_msg.what == IndiagramViewHandler.REFRESH_VIEW)
		{
			this.m_view.invalidate();
		}
	}
}