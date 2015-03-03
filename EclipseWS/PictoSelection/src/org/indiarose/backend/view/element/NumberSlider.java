package org.indiarose.backend.view.element;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.backend.ScreenManager;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NumberSlider implements OnSeekBarChangeListener
{
	protected int m_progress = 0;
	protected LinearLayout m_layout = null;
	protected TextView m_text = null;
	protected SeekBar m_seekBar = null;
	
	public NumberSlider()
	{
		m_layout = new LinearLayout(AppData.currentContext);
		m_seekBar = new SeekBar(AppData.currentContext);
		m_seekBar.setOnSeekBarChangeListener(this);
		m_text = new TextView(AppData.currentContext);
		m_text.setTextSize(ScreenManager.fontSize);
		m_text.setTextColor(Color.LTGRAY);
		
		m_layout.setOrientation(LinearLayout.VERTICAL);
		m_layout.addView(m_seekBar);
		m_layout.addView(m_text);
	}
	
	public View getView()
	{
		return m_layout;
	}

	public float getValue()
	{
		return (float) ((float)m_seekBar.getProgress() / 10.0);
	}
	
	public void setValue(float _value)
	{
		m_seekBar.setProgress((int)(_value * 10));
	}
	
	public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser)
	{
		this.m_progress = _progress;
		this.m_text.setText("" + getValue() + "s");
	}

	public void onStartTrackingTouch(SeekBar _seekBar)
	{
		
	}

	public void onStopTrackingTouch(SeekBar _seekBar)
	{
		
	}
}
