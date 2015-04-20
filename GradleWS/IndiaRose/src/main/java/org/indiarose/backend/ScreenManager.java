package org.indiarose.backend;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.Stack;

import org.indiarose.backend.activity.CategoryBrowserActivity;
import org.indiarose.backend.activity.CategoryBrowserForActivity;
import org.indiarose.backend.activity.CollectionManagement;
import org.indiarose.backend.view.IndiagramBrowser;
import org.indiarose.backend.view.element.AbstractScreen;
import org.indiarose.lib.voice.VoiceReader;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class ScreenManager
{
	public static int fontSize = 20;
	public static VoiceReader voiceReader = null;

	protected Activity m_activity = null;
	protected Stack<AbstractScreen> m_views = new Stack<AbstractScreen>();
	protected FrameLayout m_layout = null;

	protected int m_width = 0;
	protected int m_height = 0;

	public ScreenManager(Activity _main, FrameLayout _layout, int _width, int _height)
	{
		this.m_activity = _main;
		this.m_width = _width;
		this.m_height = _height;
		this.m_layout = _layout;

		//push(new Home(this));
	}

	public ScreenManager(CollectionManagement _main, FrameLayout _layout, int _width, int _height)
	{
		this.m_activity = _main;
		this.m_width = _width;
		this.m_height = _height;
		this.m_layout = _layout;

		push(new IndiagramBrowser(this));
	}
	
	public ScreenManager(CategoryBrowserActivity _main, FrameLayout _layout, int _width, int _height)
	{
		this.m_activity = _main;
		this.m_width = _width;
		this.m_height = _height;
		this.m_layout = _layout;

		push(new CategoryBrowserForActivity(this,_main));
	}

	public boolean isEmpty()
	{
		return m_views.size() == 0;
	}

	public void push(AbstractScreen _screen)
	{
		View v = _screen.getView();
		_screen.onPush();

		v.setMinimumHeight(this.m_height);
		v.setMinimumWidth(this.m_width);
		v.measure(this.m_width, this.m_height);

		this.m_views.add(_screen);
		this.m_layout.removeAllViews();
		this.m_layout.addView(v);
	}

	public void refresh(){
		Object v = this.m_views.get(m_views.size()-1);
		Log.e("Screen", ""+this.m_views.size());
		if(v != null){
			Log.e("Screen", v.getClass().toString());

			if(v instanceof IndiagramBrowser){
				Log.e("Screen", "je passe la ");
				IndiagramBrowser ib = (IndiagramBrowser)v;
				ib.onPop();
				ib.onPush();
			}
		}
		}

		public synchronized void pop()
		{
			
			if(this.m_views.size() <= 1 )
			{
				this.m_activity.finish();
			}
			else
			{
				this.m_views.pop().onPop();
				this.m_layout.removeAllViews();
				this.m_views.peek().onPush();
				this.m_layout.addView(this.m_views.peek().getView());
			}
		}

		public int getWidth()
		{
			return this.m_width;
		}

		public int getHeight()
		{
			return this.m_height;
		}

	}
