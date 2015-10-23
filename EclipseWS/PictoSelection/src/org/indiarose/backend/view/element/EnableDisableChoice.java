package org.indiarose.backend.view.element;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;

import android.graphics.Color;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EnableDisableChoice
{
	protected RadioGroup m_group = null;
	
	public EnableDisableChoice()
	{
		m_group = new RadioGroup(AppData.currentContext);
		RadioButton enable = new RadioButton(AppData.currentContext);
		RadioButton disable = new RadioButton(AppData.currentContext);
		
		enable.setTextSize(ScreenManager.fontSize);
		disable.setTextSize(ScreenManager.fontSize);
		enable.setText(R.string.enableText);
		disable.setText(R.string.disableText);
		enable.setTextColor(Color.LTGRAY);
		disable.setTextColor(Color.LTGRAY);
		enable.setId(1);
		disable.setId(0);
		
		m_group.setOrientation(RadioGroup.VERTICAL);
		m_group.addView(enable);
		m_group.addView(disable);
		
		enable.setChecked(true);
	}
	
	public boolean getValue()
	{
		return m_group.getCheckedRadioButtonId() == 1;
	}
	
	public void setValue(boolean _enable)
	{
		((RadioButton)m_group.findViewById((_enable) ? 1 : 0)).setChecked(true);
	}
	
	public View getView()
	{
		return m_group;
	}
}
