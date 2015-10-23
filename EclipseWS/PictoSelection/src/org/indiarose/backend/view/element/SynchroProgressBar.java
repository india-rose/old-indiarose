package org.indiarose.backend.view.element;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;

import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SynchroProgressBar
{
	protected TextView m_text = null;
	protected ProgressBar m_progress = null;
	protected LinearLayout m_layout = null;
	protected SynchroProgressBarHandler m_handler = null;
	
	public SynchroProgressBar()
	{
		m_handler = new SynchroProgressBarHandler(this);
		m_layout = new LinearLayout(AppData.currentContext);
		m_text = new TextView(AppData.currentContext);
		m_progress = new ProgressBar(AppData.currentContext);
		
		m_text.setTextSize(ScreenManager.fontSize);
		m_text.setTextColor(Color.LTGRAY);
		m_progress.setProgress(0);
		m_text.setText(AppData.currentContext.getString(R.string.indiagramLabel) + " 0/0");
		
		m_layout.setOrientation(LinearLayout.VERTICAL);
		m_layout.addView(m_text);
		m_layout.addView(m_progress);
	}
	
	public View getView()
	{
		return m_layout;
	}
	
	public void progress(Integer _current, Integer _max)
	{
		int current = _current.intValue();
		int max = _max.intValue();
		Message m = Message.obtain();
		m.what = SynchroProgressBarHandler.PROGRESS_MESSAGE;
		m.arg1 = current;
		m.arg2 = max;
		m_handler.sendMessage(m);
	}
	
	public void progressASync(int _current, int _max)
	{
		m_progress.setMax(_max);
		m_progress.setProgress(_current);
		m_text.setText(AppData.currentContext.getString(R.string.indiagramLabel) + " " + _current + "/" + _max);
	}
}
