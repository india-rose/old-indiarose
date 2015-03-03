package org.indiarose.backend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.view.element.AbstractScreen;
import org.indiarose.backend.view.element.ButtonItem;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.utils.ImageManager;

import storm.communication.Mapper;
import storm.communication.MapperException;
import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddIndiagram extends AbstractScreen  implements OnCheckedChangeListener, OnClickListener
{
	protected static final String LOAD_SOUND_SDCARD = "load_sound_sdcard";
	protected static final String LOAD_SOUND_MICRO = "load_sound_micro";
	protected static final String LISTEN = "listen";
	protected static final String OK = "ok";
	protected static final String CANCEL = "cancel";
	protected static final String LOAD_IMAGE_SDCARD = "load_image_sdcard";
	protected static final String LOAD_IMAGE_CAMERA = "load_image_camera";
	protected static final String PICK_CATEGORY = "pick_category";
	protected static final String HOME_CATEGORY = "home_category";
	protected static final String DELETE_SOUND = "delete_sound";
	
	protected LinearLayout m_layout = null;
	
	protected Indiagram m_indiagram = null;
	protected Category m_parent = null;
	
	protected EditText m_indiagramText = null;
	protected TextView m_indiagramSound = null;
	protected TextView m_indiagramCategory = null;
	protected ImageView m_indiagramImage = null;
	
	protected CheckBox m_indiagramIsCategory = null;
	protected LinearLayout m_categoryInfoLayout = null;
	protected LinearLayout m_indiagramTextColor = null;
	
	protected int m_categoryTextColor = Color.LTGRAY;
	protected AlertDialog m_currentDialog = null;
	
	public AddIndiagram(ScreenManager _screen)
	{
		super(_screen);
		
		
		m_indiagram = new Indiagram();
		m_parent = AppData.homeCategory;
		
		m_layout = new LinearLayout(AppData.currentContext);
		m_indiagramText = new EditText(AppData.currentContext);
		m_indiagramText.setTextSize(ScreenManager.fontSize);
		m_indiagramText.setMinimumWidth(300);
		
		m_indiagramSound = new TextView(AppData.currentContext);
		m_indiagramCategory = new TextView(AppData.currentContext);
		m_indiagramImage = new ImageView(AppData.currentContext);
		m_indiagramImage.setBackgroundColor(Color.RED);
		
		m_indiagramSound.setTextSize(ScreenManager.fontSize);
		m_indiagramCategory.setTextSize(ScreenManager.fontSize);
		m_indiagramSound.setTextColor(Color.LTGRAY);
		m_indiagramCategory.setTextColor(Color.LTGRAY);
		
		m_indiagramIsCategory = new CheckBox(AppData.currentContext);
		m_indiagramIsCategory.setOnCheckedChangeListener(this);
		m_indiagramIsCategory.setTextSize(ScreenManager.fontSize);
		m_indiagramIsCategory.setTextColor(Color.LTGRAY);
		m_indiagramIsCategory.setText(R.string.isCategoryLabel);
		m_categoryInfoLayout = new LinearLayout(AppData.currentContext);
		m_indiagramTextColor = new LinearLayout(AppData.currentContext);
		m_indiagramTextColor.setMinimumHeight(25);
		m_indiagramTextColor.setMinimumWidth(50);
		
		TextView textLabel = new TextView(AppData.currentContext);
		TextView soundLabel = new TextView(AppData.currentContext);
		TextView categoryLabel = new TextView(AppData.currentContext);
		TextView textColorLabel = new TextView(AppData.currentContext);
		
		ButtonItem buttonSoundSdcard = new ButtonItem(AppData.currentContext, LOAD_SOUND_SDCARD);
		ButtonItem buttonSoundMicro = new ButtonItem(AppData.currentContext, LOAD_SOUND_MICRO);
		ButtonItem buttonListen = new ButtonItem(AppData.currentContext, LISTEN);
		ButtonItem buttonOk = new ButtonItem(AppData.currentContext, OK);
		ButtonItem buttonCancel = new ButtonItem(AppData.currentContext, CANCEL);
		ButtonItem buttonImageSdcard = new ButtonItem(AppData.currentContext, LOAD_IMAGE_SDCARD);
		ButtonItem buttonImageCamera = new ButtonItem(AppData.currentContext, LOAD_IMAGE_CAMERA);
		ButtonItem buttonCategoryPick = new ButtonItem(AppData.currentContext, PICK_CATEGORY);
		ButtonItem buttonHomeCategory = new ButtonItem(AppData.currentContext, HOME_CATEGORY);
		ButtonItem buttonDeleteSound = new ButtonItem(AppData.currentContext, DELETE_SOUND);
		
		//TODO : enable sdcard button if necessary
		buttonSoundSdcard.setVisibility(View.GONE);
		buttonImageSdcard.setVisibility(View.GONE);
		
		TextView [] views = new TextView[]{
			textLabel, soundLabel, categoryLabel, textColorLabel,
			buttonListen, buttonOk, buttonCancel, buttonCategoryPick, buttonHomeCategory,
			buttonSoundSdcard, buttonSoundMicro, buttonImageCamera, buttonImageSdcard,
			buttonDeleteSound
		};
		int [] resId = new int[]{
			R.string.indiagramTextLabel, R.string.indiagramSoundLabel, R.string.indiagramCategoryLabel, R.string.indiagramTextColorLabel,
			R.string.listenButton, R.string.okText, R.string.cancelText, R.string.pickCategoryButton, R.string.homeCategoryButton,
			R.string.loadSoundSdcard, R.string.recordSound, R.string.takeImage, R.string.loadImageSdcard,
			R.string.deleteText
		};
		for(int i = 0 ; i < views.length ; ++i)
		{
			TextView v = views[i];
			v.setTextSize(ScreenManager.fontSize);
			v.setText(resId[i]);
			if(v instanceof ButtonItem)
			{
				v.setTextColor(Color.BLACK);
				
				try
				{
					Mapper.connect(v, "clicked", this, "buttonEvent");
				}
				catch(MapperException e)
				{
					Log.wtf("AddIndiagram", "unable to connect button", e);
				}
			}
			else
			{
				v.setTextColor(Color.LTGRAY);
			}
		}
		
		
		LinearLayout textLayout = new LinearLayout(AppData.currentContext);
		textLayout.setOrientation(LinearLayout.HORIZONTAL);
		textLayout.addView(textLabel);
		textLayout.addView(m_indiagramText);
		LinearLayout soundLayout = new LinearLayout(AppData.currentContext);
		soundLayout.setOrientation(LinearLayout.HORIZONTAL);
		soundLayout.addView(soundLabel);
		soundLayout.addView(m_indiagramSound);
		LinearLayout soundButtonLayout = new LinearLayout(AppData.currentContext);
		soundButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
		soundButtonLayout.addView(buttonSoundSdcard);
		soundButtonLayout.addView(buttonSoundMicro);
		soundButtonLayout.addView(buttonListen);
		soundButtonLayout.addView(buttonDeleteSound);
		LinearLayout categoryNameLayout = new LinearLayout(AppData.currentContext);
		categoryNameLayout.setOrientation(LinearLayout.HORIZONTAL);
		categoryNameLayout.addView(categoryLabel);
		categoryNameLayout.addView(m_indiagramCategory);
		LinearLayout categoryButtonLayout = new LinearLayout(AppData.currentContext);
		categoryButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
		categoryButtonLayout.addView(buttonCategoryPick);
		categoryButtonLayout.addView(buttonHomeCategory);
		LinearLayout actionButtonLayout = new LinearLayout(AppData.currentContext);
		actionButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
		actionButtonLayout.addView(buttonOk);
		actionButtonLayout.addView(buttonCancel);
		
		LinearLayout leftPart = new LinearLayout(AppData.currentContext);
		leftPart.setOrientation(LinearLayout.VERTICAL);
		leftPart.setGravity(Gravity.CENTER);
		leftPart.addView(textLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(soundLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(soundButtonLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(categoryNameLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(categoryButtonLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		leftPart.addView(actionButtonLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		
		LinearLayout imageButtonLayout = new LinearLayout(AppData.currentContext);
		imageButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
		imageButtonLayout.setGravity(Gravity.CENTER);
		imageButtonLayout.addView(buttonImageSdcard);
		imageButtonLayout.addView(buttonImageCamera);
		
		m_categoryInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
		m_categoryInfoLayout.addView(textColorLabel);
		LinearLayout colorLayout = new LinearLayout(AppData.currentContext);
		colorLayout.setGravity(Gravity.CENTER);
		colorLayout.setMinimumWidth(100);
		colorLayout.setMinimumHeight(25);
		colorLayout.addView(m_indiagramTextColor);
		m_indiagramTextColor.setOnClickListener(this);
		m_categoryInfoLayout.addView(colorLayout);
		
		LinearLayout rightPart = new LinearLayout(AppData.currentContext);
		rightPart.setOrientation(LinearLayout.VERTICAL);
		rightPart.setGravity(Gravity.CENTER);
		rightPart.addView(m_indiagramImage, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		rightPart.addView(imageButtonLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		rightPart.addView(m_indiagramIsCategory, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		rightPart.addView(m_categoryInfoLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		
		leftPart.setMinimumHeight(this.m_screen.getHeight());
		leftPart.setMinimumWidth(this.m_screen.getWidth() / 2);
		rightPart.setMinimumHeight(this.m_screen.getHeight());
		rightPart.setMinimumWidth(this.m_screen.getWidth() / 2);
		m_layout.setOrientation(LinearLayout.HORIZONTAL);
		m_layout.addView(leftPart);
		m_layout.addView(rightPart);
		
		init();
	}

	protected void init()
	{
		m_indiagramText.setText(m_indiagram.text);
		if(this.m_indiagram instanceof Category)
		{
			m_categoryTextColor = ((Category) this.m_indiagram).textColor;
			m_indiagramIsCategory.setChecked(true);
			m_categoryInfoLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			m_indiagramIsCategory.setChecked(false);
			m_categoryInfoLayout.setVisibility(View.INVISIBLE);
		}
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
		if(TextUtils.isEmpty(this.m_indiagram.soundPath))
		{
			m_indiagramSound.setText(R.string.indiagramNoSound);
		}
		else
		{
			m_indiagramSound.setText(this.m_indiagram.soundPath);
		}
		m_indiagramCategory.setText(m_parent.text);
		if(m_indiagramIsCategory.isChecked())
		{
			m_indiagramTextColor.setBackgroundColor(m_categoryTextColor);
		}
		
		m_indiagramImage.setMinimumHeight(AppData.settings.indiagramSize);
		m_indiagramImage.setMinimumWidth(AppData.settings.indiagramSize);
		if(!TextUtils.isEmpty(m_indiagram.imagePath))
		{
			try
			{
				m_indiagramImage.setImageBitmap(ImageManager.loadImage(PathData.IMAGE_DIRECTORY + m_indiagram.imagePath, AppData.settings.indiagramSize, AppData.settings.indiagramSize));
			} 
			catch (Exception e)
			{
				Log.wtf("AddIndiagram", "Unable to load image", e);
			}
		}
	}
	
	public synchronized void buttonEvent(ButtonItem _button)
	{
		String id = _button.getIdentifier();
		if(id.equals(CANCEL))
		{
			this.m_screen.pop();
		}
		else if(id.equals(HOME_CATEGORY))
		{
			m_parent = AppData.homeCategory;
			refresh();
		}
		else if(id.equals(LISTEN))
		{
			this.m_indiagram.text = this.m_indiagramText.getText().toString();
			ScreenManager.voiceReader.read(this.m_indiagram);
		}
		else if(id.equals(LOAD_IMAGE_CAMERA))
		{
			if(!AppData.currentContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
			{
				Toast.makeText(AppData.currentContext, R.string.noCameraMessage, Toast.LENGTH_LONG).show();
			} 
			else 
			{
				TakePhoto photo = new TakePhoto(this.m_screen);
				try
				{
					Mapper.connect(photo, "photoTaken", this, "savePhoto");
				} 
				catch (MapperException e)
				{
					Log.wtf("AddIndiagram", "unable to connect photo", e);
				}
				this.m_screen.push(photo);
			}
		}
		else if(id.equals(LOAD_IMAGE_SDCARD))
		{
			// Not implemented yet
		}
		else if(id.equals(LOAD_SOUND_MICRO))
		{
			if(!AppData.currentContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
			{
				Toast.makeText(AppData.currentContext, R.string.noMicrophoneMessage, Toast.LENGTH_LONG).show();
			}
			else
			{
				RecordSound sound = new RecordSound(this.m_screen);
				try
				{
					Mapper.connect(sound, "soundRecorded", this, "saveSound");
				} 
				catch (MapperException e)
				{
					Log.wtf("AddIndiagram", "Unable to connect soundrecorder", e);
				}
				this.m_screen.push(sound);
			}
		}
		else if(id.equals(LOAD_SOUND_SDCARD))
		{
			//TODO : see how to.
		}
		else if(id.equals(OK))
		{
			if(saveIndiagram())
			{
				this.m_screen.pop();
			}
		}
		else if(id.equals(PICK_CATEGORY))
		{
			CategoryBrowser picker = new CategoryBrowser(this.m_screen);
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
		else if(id.equals(DELETE_SOUND))
		{
			AlertDialog.Builder adb = new AlertDialog.Builder(AppData.currentContext);
			
			adb.setTitle(R.string.deleteSoundQuestion);
			adb.setIcon(android.R.drawable.ic_dialog_alert);
			adb.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface _dialog, int _which) 
				{
					saveSound("");
					refresh();
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
	
	protected void closeDialog()
	{
		this.m_currentDialog.dismiss();
	}
	
	public void saveSound(String _path)
	{
		this.m_indiagram.soundPath = _path;
	}
	
	public void savePhoto(String _path)
	{
		this.m_indiagram.imagePath = _path;
	}
	
	public void categoryChanged(Category _category)
	{
		m_parent = _category;
	}

	protected boolean saveIndiagram()
	{
		Indiagram newOne = null;
		if(m_indiagramIsCategory.isChecked())
		{
			Category c = new Category(m_indiagramText.getText().toString(), m_indiagram.imagePath, m_indiagram.soundPath, m_categoryTextColor);
			m_parent.indiagrams.add(c);
			newOne = c;
		}
		else
		{
			this.m_indiagram.text = m_indiagramText.getText().toString();
			m_parent.indiagrams.add(this.m_indiagram);
			newOne = this.m_indiagram;
		}
		try
		{
			AppData.SaveHomeCategory();
		} 
		catch (Exception e)
		{
			Log.wtf("AddIndiagram", "Unable to save", e);
		}
		AppData.changeLog.addIndiagram(newOne);
		return true;
	}
	
	/**
	 * Method called when the user click the check box to enable/disable category
	 */
	public void onCheckedChanged(CompoundButton _checkbox, boolean _state)
	{
		if(_state)
		{
			m_categoryInfoLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			m_categoryInfoLayout.setVisibility(View.INVISIBLE);
		}
		refresh();
	}

	/**
	 * Method called when the user click the category text color.
	 */
	public void onClick(View _view)
	{
		final ColorPickerDialog colorDialog = new ColorPickerDialog(AppData.currentContext, m_categoryTextColor);

		colorDialog.setAlphaSliderVisible(true);
		colorDialog.setTitle(AppData.currentContext.getString(R.string.chooseColor));
		colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, AppData.currentContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_categoryTextColor = colorDialog.getColor();
	        	m_indiagramTextColor.setBackgroundColor(m_categoryTextColor);
			}
		});

		colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, AppData.currentContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Nothing to do here.
			}
		});

		colorDialog.show();
	}
}
