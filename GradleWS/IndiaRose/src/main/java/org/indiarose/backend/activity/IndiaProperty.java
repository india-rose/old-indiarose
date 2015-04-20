package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.indiarose.R;
import org.indiarose.lib.AppData;
import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import org.indiarose.lib.view.IndiagramView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class IndiaProperty extends Activity implements OnCheckedChangeListener, OnClickListener {



	protected static final String FONT_DIRECTORY = "/system/fonts";

	protected int m_reinforcerColor = 0;
	protected Hashtable<String, String> fontsList = null;

	protected View buttonColorReinforcer = null;
	protected View buttonOk = null;
	protected View buttonCancel = null;
	protected CheckBox checkboxReinforcer = null;
	protected CheckBox checkboxHomeCategory = null;
	protected CheckBox checkboxMove = null;

	protected Spinner spinnerImageSize = null;
	protected Spinner spinnerFontSize = null;
	protected Spinner spinnerFont = null;

	protected LinearLayout reinforcerArea = null;
	protected LinearLayout indiagramArea = null;
	
	protected ViewGroup leftLayout = null;
	protected ViewGroup rightLayout = null ;
	
	protected boolean initialize = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_india_property);
		AppData.current_activity = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		m_reinforcerColor = AppData.settings.backgroundReinforcerReading;
		
		ajouterVues();
		ajouterListeners();
		charger();
	}
	
	protected void ajouterVues(){
		buttonColorReinforcer = findViewById(R.id.button_reinforcer_color);
		buttonOk = findViewById(R.id.button_ok);
		buttonCancel = findViewById(R.id.button_cancel);
		
		checkboxReinforcer = (CheckBox) findViewById(R.id.checkboxreinforcer);
		checkboxHomeCategory = (CheckBox) findViewById(R.id.checkboxhomecategory);
		checkboxMove = (CheckBox) findViewById(R.id.checkboxMove);
		
		leftLayout = (ViewGroup) findViewById(R.id.relative_left);
		rightLayout = (ViewGroup) findViewById(R.id.relative_right);
	}
	
	protected void ajouterListeners(){
		
		buttonOk.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		buttonColorReinforcer.setOnClickListener(this);

		checkboxReinforcer.setOnCheckedChangeListener(this);
		checkboxHomeCategory.setOnCheckedChangeListener(this);
		checkboxMove.setOnCheckedChangeListener(this);
	}

	protected void charger(){
		reinforcerArea = new LinearLayout(this);
		indiagramArea = new LinearLayout(this);

		createSpinner();

		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightLayout.addView(reinforcerArea, rlp);

		rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rightLayout.addView(indiagramArea, rlp);

		
	}
	
	@Override
	public void onWindowFocusChanged (boolean hasFocus) {      
		if(!initialize){
			initialize = true;
			createSpinner();
		}
		
		checkboxReinforcer.setChecked(AppData.settings.enableReadingReinforcer);
		checkboxHomeCategory.setChecked(AppData.settings.enableBackHome);
		checkboxHomeCategory.setChecked(AppData.settings.enableDragAndDrop); //pas sur

	}
	
	protected void createSpinner()
	{
		//create spinner for indiagram size
		this.spinnerImageSize = (Spinner) this.findViewById(R.id.spinnerImageSize);
		this.spinnerFontSize = (Spinner) this.findViewById(R.id.spinnerSizePolicy);
		this.spinnerFont = (Spinner) this.findViewById(R.id.spinnerPolicy);

		ArrayAdapter<String> imageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		final ArrayAdapter<String> fontSizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		ArrayAdapter<String> fontAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

		int currentSize = 0;
		int currentFontSize = 0;
		int currentFont = 0;

		int [] imageSizes = new int[]{32, 48, 64, 80, 128, 160, 200, 256, 280, 300};
		for(int i = 0 ; i < imageSizes.length ; ++i)
		{
			if(95-(int) (((imageSizes[i] / 10 * 2 + imageSizes[i]  + 8 * (((8*25/imageSizes[i] )/2)+1))*1.2)
					/ getWindow().getDecorView().getHeight() * 100)>48)
			{
				if(imageSizes[i] == AppData.settings.indiagramSize)
				{
					currentSize = i;
				}
				imageAdapter.add(imageSizes[i] + "x" + imageSizes[i]);
			}
		}

		final ArrayList<Integer> fontSize = new ArrayList<Integer>();
		for(int i = 8 ; i <= 32 ; i += 2)
		{
			 if (	(95-(int) (((AppData.settings.indiagramSize / 10 * 2 + AppData.settings.indiagramSize  + i * (((i*25/AppData.settings.indiagramSize )/2)+1))*1.2)
					/ getWindow().getDecorView().getHeight() * 100)>48))
			{
					fontSize.add(i);
			}
			
		}
		for(int i = 0 ; i < fontSize.size() ; ++i)
		{
			if(fontSize.get(i) == AppData.settings.fontSize)
			{
				currentFontSize = i;
			}
			fontSizeAdapter.add(fontSize.get(i) + "");
		}

		fontsList = getFonts();
		Enumeration<String> keys = fontsList.keys();
		for(int i = 0 ; keys.hasMoreElements() ; ++i)
		{
			String next = keys.nextElement();
			if(fontsList.get(next).equals(AppData.settings.fontFamily))
			{
				currentFont = i;
			}
			fontAdapter.add(next);
		}
		
		this.spinnerImageSize.setAdapter(imageAdapter);
		this.spinnerImageSize.setSelection(currentSize);
		
		this.spinnerImageSize.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public synchronized void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	String selected = ((TextView) spinnerImageSize.getSelectedView()).getText().toString();
		    	AppData.settings.indiagramSize = Integer.parseInt(selected.substring(0, selected.indexOf("x")));
				fontSize.removeAll(fontSize);
		    	for(int i = 8 ; i <= 32 ; i += 2)
				{
					 if (	(95-(int) (((AppData.settings.indiagramSize / 10 * 2 + AppData.settings.indiagramSize  + i * (((i*25/AppData.settings.indiagramSize )/2)+1))*1.2)
							/ getWindow().getDecorView().getHeight() * 100)>48))
					{
							fontSize.add(i);
					}
					
				}
		    	fontSizeAdapter.clear();
				for(int i = 0 ; i < fontSize.size() ; ++i)
				{
					fontSizeAdapter.add(fontSize.get(i) + "");
				}
		    	fontSizeAdapter.notifyDataSetChanged();
		    	refreshPreview();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		this.spinnerFontSize.setAdapter(fontSizeAdapter);
		this.spinnerFontSize.setSelection(currentFontSize);
		this.spinnerFont.setAdapter(fontAdapter);
		this.spinnerFont.setSelection(currentFont);
	}

	protected Hashtable<String, String> getFonts() 
	{
		File f = new File(FONT_DIRECTORY);
		File[] files = f.listFiles();
		Hashtable<String, String> result = new Hashtable<String, String>();
		for(File file : files)
		{
			if(file.getName().endsWith(".ttf") )
			{
				result.put(file.getName(), file.getPath());
			}
		}
		return result;
	}

	public void onCheckedChanged(CompoundButton _checkbox, boolean _value)
	{
		buttonColorReinforcer.setEnabled(_checkbox.isChecked());
		refreshPreview();
	}

	protected void refreshPreview()
	{
		if(spinnerImageSize.getSelectedView() != null)
		{
			String selected = ((TextView) spinnerImageSize.getSelectedView()).getText().toString();
			int size = Integer.parseInt(selected.substring(0, selected.indexOf("x")));

			ViewGroup.LayoutParams imageParam = indiagramArea.getLayoutParams();
			ViewGroup.LayoutParams reinforcerParam = reinforcerArea.getLayoutParams();

			imageParam.height = size;
			imageParam.width = size;

			reinforcerParam.height = (int)(size * 1.2);
			reinforcerParam.width = (int)(size * 1.2);

			indiagramArea.setLayoutParams(imageParam);
			reinforcerArea.setLayoutParams(reinforcerParam);
		}

		indiagramArea.setBackgroundColor(Color.RED);
		reinforcerArea.setBackgroundColor((checkboxReinforcer.isChecked()) ? m_reinforcerColor : Color.TRANSPARENT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.india_property, menu);
		return true;
	}

	protected void save(){
		AppData.settings.fontFamily = fontsList.get(((TextView) spinnerFont.getSelectedView()).getText().toString());
		AppData.settings.fontSize = Integer.parseInt(((TextView) spinnerFontSize.getSelectedView()).getText().toString());
		AppData.settings.enableReadingReinforcer = checkboxReinforcer.isChecked();
		AppData.settings.enableBackHome = checkboxHomeCategory.isChecked();
		AppData.settings.enableDragAndDrop= checkboxMove.isChecked();
		AppData.settings.backgroundReinforcerReading = m_reinforcerColor;
		String selected = ((TextView) spinnerImageSize.getSelectedView()).getText().toString();
		AppData.settings.indiagramSize = Integer.parseInt(selected.substring(0, selected.indexOf("x")));

		int heightTop = (int)(AppData.settings.heightSelectionArea / 100.0 * this.leftLayout.getHeight());
		int heightBottom = this.leftLayout.getHeight() - heightTop;
		Log.wtf("IndiaView", "Original top height : " + AppData.settings.heightSelectionArea);
		if(heightTop < AppData.settings.indiagramSize * 1.2)
		{
			AppData.settings.heightSelectionArea = (int)(IndiagramView.getDefaultHeight() * 1.2 / this.leftLayout.getHeight() * 100);
		}
		else if(heightBottom < AppData.settings.indiagramSize * 1.2)
		{
			AppData.settings.heightSelectionArea = 100 - (int)(IndiagramView.getDefaultHeight() * 1.2 / this.leftLayout.getHeight() * 100);
		}
		Log.wtf("IndiaView", "new top height  : " + AppData.settings.heightSelectionArea);

		AppData.settingsChanged();
	}
	
	protected void activateReinforceColor(){
		if(checkboxReinforcer.isChecked())
		{
			final ColorPickerDialog colorDialog = new ColorPickerDialog(this, m_reinforcerColor);

			colorDialog.setAlphaSliderVisible(true);
			colorDialog.setTitle(getString(R.string.chooseColor));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					m_reinforcerColor = colorDialog.getColor();
		        	refreshPreview();
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Nothing to do here.
				}
			});

			colorDialog.show();
		} 
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_ok:
			save();
			onBackPressed();
			break;

		case R.id.button_cancel:
			onBackPressed();
			break;
			
		case R.id.button_reinforcer_color:
			activateReinforceColor();
			break;
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		Intent i = new Intent(this, AppSettings.class);
		startActivity(i);
		finish();
	}

}
