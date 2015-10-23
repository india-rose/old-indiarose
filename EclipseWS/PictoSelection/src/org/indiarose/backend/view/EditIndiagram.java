package org.indiarose.backend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.lib.CollectionManager;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.view.element.ButtonItem;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.util.Log;
import android.widget.Toast;

/**
 * Signal : 
 * 	- indiagramChanged(Indiagram _newOne) : signal raised when indiagram instance change while edit process.
 * @author Julien
 *
 */
public class EditIndiagram extends AddIndiagram
{
	protected Indiagram m_originalIndiagram = null;
	protected Category m_originalParent = null;

	public EditIndiagram(ScreenManager _screen, Indiagram _indiagram)
	{
		super(_screen);

		this.m_originalIndiagram = _indiagram;
		this.m_originalParent = Indiagram.getParent(_indiagram);
		this.m_parent = m_originalParent;
		this.m_indiagram = _indiagram.clone();
		init();
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
			Toast.makeText(AppData.currentContext, R.string.canNotSaveCategory, Toast.LENGTH_LONG).show();
			return false;
		}
		
		if((m_indiagramIsCategory.isChecked() && this.m_originalIndiagram instanceof Category) ||
			(!m_indiagramIsCategory.isChecked() && !(this.m_originalIndiagram instanceof Category)))
		{
			// if indiagram has changed parent category
			if(!m_originalParent.equals(this.m_parent))
			{
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

	public synchronized void buttonEvent(ButtonItem _button)
	{
		if(_button.getIdentifier().equals(PICK_CATEGORY))
		{
			CategoryBrowser picker = new CategoryBrowser(this.m_screen);
			picker.addToIgnoreList(m_originalIndiagram);
			try
			{
				Mapper.connect(picker, "selected", this, "categoryChanged");
			} 
			catch (MapperException e)
			{
				Log.wtf("AddIndiagram", "unable to connect CategoryBrowser", e);
			}
			this.m_screen.push(picker);
		}
		else
		{
			super.buttonEvent(_button);
		}
	}
}
