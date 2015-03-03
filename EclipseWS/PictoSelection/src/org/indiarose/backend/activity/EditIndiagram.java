package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.R;
import org.indiarose.lib.AppData;
import org.indiarose.lib.CollectionManager;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class EditIndiagram extends AddIndiagram {

	protected Indiagram m_originalIndiagram = null;
	protected Category m_originalParent = null;
	
	public static Indiagram ignore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Indiagram _indiagram = AppData.current_indiagram;
    	AppData.current_activity = this;
		this.m_originalIndiagram = _indiagram;
		this.m_originalParent = Indiagram.getParent(_indiagram);
		this.m_parent = m_originalParent;
		this.m_indiagram = _indiagram.clone();
		init();
		m_indiagramIsCategory.setVisibility(View.GONE);
	}
	
	@Override
	protected boolean saveIndiagram()
	{
		// save change in AppData.changeLog
		AppData.changeLog.changeIndiagram(this.m_originalIndiagram);
		if(!m_originalParent.equals(this.m_parent))
		{
			AppData.changeLog.changeIndiagram(this.m_originalParent);
			AppData.changeLog.changeIndiagram(this.m_parent);
		}
		
		// if indiagram is not a category anymore but have child, cancel save process.
		if(!m_indiagramIsCategory.isChecked() && this.m_originalIndiagram instanceof Category && ((Category)this.m_originalIndiagram).indiagrams.size() > 0)
		{
			Toast.makeText(this, R.string.canNotSaveCategory, Toast.LENGTH_LONG).show();
			return false;
		}
		
		if((m_indiagramIsCategory.isChecked() && this.m_originalIndiagram instanceof Category) ||
			(!m_indiagramIsCategory.isChecked() && !(this.m_originalIndiagram instanceof Category)))
		{
			// if indiagram has changed parent category
			if(!m_originalParent.equals(this.m_parent))
			{
				Log.d("Je change de category",m_originalParent.text + " new parent " +this.m_parent.text);
				CollectionManager.ChangeCategory(this.m_originalIndiagram, this.m_originalParent, this.m_parent);
			}
			
			// no type change
			this.m_originalIndiagram.text = this.m_indiagramText.getText().toString();
			this.m_originalIndiagram.soundPath = this.m_indiagram.soundPath;
			this.m_originalIndiagram.imagePath = this.m_indiagram.imagePath;
			if(this.m_originalIndiagram instanceof Category)
			{
				((Category)this.m_originalIndiagram).textColor = this.m_categoryTextColor;
			}
		}
		else
		{
			// type has changed
			CollectionManager.Delete(this.m_originalIndiagram, this.m_originalParent);
			if(this.m_originalIndiagram instanceof Category)
			{
				//transform to standard indiagram
				Indiagram n = new Indiagram(this.m_indiagramText.getText().toString(), this.m_indiagram.imagePath, this.m_indiagram.soundPath);
				n.filePath = this.m_originalIndiagram.filePath;
				this.m_parent.indiagrams.add(n);
				
				try
				{
					Mapper.emit(this, "indiagramChanged", n);
				} 
				catch (MapperException e)
				{
					Log.wtf("EditIndiagram", "unable to emit change indiagram", e);
				}
			}
			else
			{
				//transform to category
				Category c = new Category(this.m_indiagramText.getText().toString(), this.m_indiagram.imagePath, this.m_indiagram.soundPath, this.m_categoryTextColor);
				c.filePath = this.m_originalIndiagram.filePath;
				this.m_parent.indiagrams.add(c);
				
				try
				{
					Mapper.emit(this, "indiagramChanged", c);
				} 
				catch (MapperException e)
				{
					Log.wtf("EditIndiagram", "unable to emit change indiagram", e);
				}
			}
			
		}

		try
		{
			AppData.SaveHomeCategory();
		} 
		catch (Exception e)
		{
			Log.wtf("AddIndiagram", "Unable to save", e);
		}
		return true;
	}

	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.chooseCategory:
			ignore = m_originalIndiagram;
			Intent i = new Intent(this,CategoryBrowserActivity.class);
			i.putExtra("ignore", true);
			startActivityForResult(i, CATEGORY_REQUEST);
			break;

		default:
			super.onClick(v);
			break;
		}
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_indiagram, menu);
		return true;
	}
	


}
