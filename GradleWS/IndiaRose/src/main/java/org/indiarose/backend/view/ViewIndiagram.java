
/*
package org.indiarose.backend.view;

import java.io.File;

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.backend.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.view.element.AbstractScreen;
import org.indiarose.backend.view.element.ButtonItem;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.utils.ImageManager;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewIndiagram extends AbstractScreen
{
	protected static final String LISTEN = "listen";
	protected static final String BACK = "back";
	protected static final String EDIT = "edit";
	protected static final String DELETE = "delete";
	
	protected Indiagram m_indiagram = null;
	protected LinearLayout m_layout = null;
	
	protected TextView m_indiagramText = null;
	protected TextView m_indiagramSound = null;
	protected TextView m_indiagramCategory = null;
	
	protected ImageView m_indiagramImage = null;
	protected LinearLayout m_indiagramTextColor = null;
	
	protected AlertDialog m_currentDialog = null;
	
	protected LinearLayout colorLayout = null;
	
	public ViewIndiagram(ScreenManager _screen, Indiagram _indiagram)
	{
		super(_screen);
		
		this.m_indiagram = _indiagram;
		
		m_indiagramText = new TextView(AppData.currentContext);
		m_indiagramSound = new TextView(AppData.currentContext);
		m_indiagramCategory = new TextView(AppData.currentContext);
		m_indiagramImage = new ImageView(AppData.currentContext);
		m_indiagramImage.setScaleType(ScaleType.CENTER_INSIDE);
		m_indiagramImage.setMaxHeight(AppData.settings.indiagramSize);
		m_indiagramImage.setMaxWidth(AppData.settings.indiagramSize);
		m_indiagramImage.setMinimumHeight(AppData.settings.indiagramSize);
		m_indiagramImage.setMinimumWidth(AppData.settings.indiagramSize);
		
		ButtonItem listenButton = new ButtonItem(AppData.currentContext, LISTEN);
		ButtonItem backButton = new ButtonItem(AppData.currentContext, BACK);
		ButtonItem editButton = new ButtonItem(AppData.currentContext, EDIT);
		ButtonItem deleteButton = new ButtonItem(AppData.currentContext, DELETE);
		
		listenButton.setText(R.string.listenButton);
		backButton.setText(R.string.backText);
		editButton.setText(R.string.editText);
		deleteButton.setText(R.string.deleteText);
		
		TextView [] views = new TextView[]{
				m_indiagramText, m_indiagramSound, m_indiagramCategory, 
				listenButton, backButton, editButton, deleteButton
		};
		for(TextView v : views)
		{
			v.setTextSize(ScreenManager.fontSize);
			
			if(v instanceof ButtonItem)
			{
				v.setTextColor(Color.BLACK);
				try
				{
					Mapper.connect(v, "clicked", this, "buttonEvent");
				}
				catch(MapperException e)
				{
					Log.wtf("ViewIndiagram", "unable to connect button", e);
				}
			}
			else
			{
				v.setTextColor(Color.LTGRAY);
			}
		}
		
		
		LinearLayout leftPart = new LinearLayout(AppData.currentContext);
		leftPart.setOrientation(LinearLayout.VERTICAL);
		leftPart.setGravity(Gravity.CENTER);
		leftPart.setMinimumHeight(this.m_screen.getHeight());
		leftPart.setMinimumWidth(this.m_screen.getWidth() / 2);
		
		LinearLayout buttonLayout = new LinearLayout(AppData.currentContext);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.addView(backButton);
		buttonLayout.addView(editButton);
		buttonLayout.addView(deleteButton);
		
		leftPart.addView(m_indiagramText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(m_indiagramCategory, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(m_indiagramSound, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(listenButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(buttonLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		LinearLayout rightPart = new LinearLayout(AppData.currentContext);
		rightPart.setOrientation(LinearLayout.VERTICAL);
		rightPart.setGravity(Gravity.CENTER);
		rightPart.setMinimumHeight(this.m_screen.getHeight());
		rightPart.setMinimumWidth(this.m_screen.getWidth() / 2);
		
		rightPart.addView(m_indiagramImage);
		
		
		m_indiagramTextColor = new LinearLayout(AppData.currentContext);
		TextView textColorLabel = new TextView(AppData.currentContext);
		
		textColorLabel.setTextSize(ScreenManager.fontSize);
		textColorLabel.setText(R.string.indiagramTextColorLabel);
		textColorLabel.setTextColor(Color.LTGRAY);
		
		m_indiagramTextColor.setMinimumHeight(25);
		m_indiagramTextColor.setMinimumWidth(50);
		
		LinearLayout fakeLayout = new LinearLayout(AppData.currentContext);
		fakeLayout.setMinimumHeight(25);
		fakeLayout.setMinimumWidth(100);
		fakeLayout.setGravity(Gravity.CENTER);
		fakeLayout.addView(m_indiagramTextColor);
	
		colorLayout = new LinearLayout(AppData.currentContext);
		colorLayout.setOrientation(LinearLayout.HORIZONTAL);
		colorLayout.setGravity(Gravity.CENTER);
		colorLayout.addView(textColorLabel);
		colorLayout.addView(fakeLayout);
		
		rightPart.addView(colorLayout);
		
		m_layout = new LinearLayout(AppData.currentContext);
		m_layout.setOrientation(LinearLayout.HORIZONTAL);
		m_layout.addView(leftPart);
		m_layout.addView(rightPart);
	}

	@Override
	public View getView()
	{
		return m_layout;
	}
	
	@Override
	public void onPush()
	{
		refresh();
	}
	
	protected void refresh()
	{
		m_indiagramText.setText(AppData.currentContext.getString(R.string.indiagramTextLabel) + " " + m_indiagram.text);
		if(TextUtils.isEmpty(m_indiagram.soundPath))
		{
			m_indiagramSound.setText(AppData.currentContext.getString(R.string.indiagramSoundLabel) + " " + AppData.currentContext.getString(R.string.indiagramNoSound));
		}
		else
		{
			m_indiagramSound.setText(AppData.currentContext.getString(R.string.indiagramSoundLabel) + " " + m_indiagram.soundPath);
		}
		m_indiagramCategory.setText(AppData.currentContext.getString(R.string.indiagramCategoryLabel) + " " + Indiagram.getParent(m_indiagram).text);
		
		try
		{
			m_indiagramImage.setImageBitmap(ImageManager.loadImage(PathData.IMAGE_DIRECTORY + this.m_indiagram.imagePath, AppData.settings.indiagramSize, AppData.settings.indiagramSize));
		} 
		catch (Exception e)
		{
			Log.wtf("ViewIndiagram", "unable to load image : " + this.m_indiagram.imagePath, e);
		}
		
		if(m_indiagram instanceof Category)
		{
			colorLayout.setVisibility(LinearLayout.VISIBLE);
			m_indiagramTextColor.setBackgroundColor(((Category)m_indiagram).textColor);
		}
		else
		{
			colorLayout.setVisibility(LinearLayout.GONE);
		}
	}
	
	public void buttonEvent(ButtonItem _button)
	{
		String id = _button.getIdentifier();
		if(id.equals(LISTEN))
		{
			ScreenManager.voiceReader.read(this.m_indiagram);
		}
		else if(id.equals(BACK))
		{
			this.m_screen.pop();
		}
		else if(id.equals(DELETE))
		{
			boolean ok = true;
			if(this.m_indiagram instanceof Category)
			{
				if(((Category) this.m_indiagram).indiagrams.size() > 0)
				{
					Toast.makeText(AppData.currentContext, R.string.canNotDeleteCategory, Toast.LENGTH_LONG).show();
					ok = false;
				}
			}
			
			if(ok)
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(AppData.currentContext);
				
				adb.setTitle(R.string.deleteQuestion);
				adb.setIcon(android.R.drawable.ic_dialog_alert);
				adb.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface _dialog, int _which) 
					{
						delete(m_indiagram);
						m_screen.pop();
					} 
				});
				adb.setNegativeButton(R.string.cancelText, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface _dialog, int _which) 
					{
						closeDialog();
					} 
				});

				adb.setOnCancelListener(new DialogInterface.OnCancelListener() 
				{
					public void onCancel(DialogInterface _dialog) 
					{
						closeDialog();
					}
				});
				this.m_currentDialog = adb.show();
			}
		}
		else if(id.equals(EDIT))
		{
			EditIndiagram edit = new EditIndiagram(this.m_screen, this.m_indiagram);
			try
			{
				Mapper.connect(edit, "indiagramChanged", this, "changeIndiagram");
			} 
			catch (MapperException e)
			{
				Log.wtf("ViewIndiagram", "unable to connect edit screen", e);
			}
			this.m_screen.push(edit);
		}
	}
	
	public void changeIndiagram(Indiagram _indiagram)
	{
		this.m_indiagram = _indiagram;
	}
	
	protected void closeDialog()
	{
		this.m_currentDialog.dismiss();
	}
	
	protected void delete(Indiagram _indiagram)
	{
		AppData.changeLog.deleteIndiagram(_indiagram);
		//find parent and delete category
		Indiagram parent = Indiagram.getParent(_indiagram);
		if(parent != null)
		{
			AppData.changeLog.changeIndiagram(parent);
			((Category)parent).indiagrams.remove(_indiagram);
		}
		
		File f = new File(PathData.XML_DIRECTORY + _indiagram.filePath);
		f.delete();
		
		try
		{
			AppData.SaveHomeCategory();
		} 
		catch (Exception e)
		{
			Log.wtf("ViewIndiagram", "Unable to save collection", e);
		}
	}
}
*/
