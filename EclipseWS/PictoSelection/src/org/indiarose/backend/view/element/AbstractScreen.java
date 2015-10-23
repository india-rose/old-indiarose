package org.indiarose.backend.view.element;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.backend.ScreenManager;

import android.view.View;

public abstract class AbstractScreen
{
	protected ScreenManager m_screen = null;
	
	public AbstractScreen(ScreenManager _screen)
	{
		this.m_screen = _screen;
	}
	
	public abstract View getView();
	
	public void onPush()
	{
		
	}
	
	public void onPop()
	{
		
	}
}
