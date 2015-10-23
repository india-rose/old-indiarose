package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.acra.sender.GoogleFormSender;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.alert.RecordSound;
import org.indiarose.backend.camera.PhotoHandler;
import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.utils.ImageManager;
import org.indiarose.lib.voice.VoiceActivity;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddIndiagram extends VoiceActivity implements OnCheckedChangeListener, OnClickListener {

	public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
	public static final int RECORD_SOUND = 1199;
	private static final int IMAGE_REQUEST = 1889; 
	private static final int SOUND_REQUEST = 2289; 

	private File _photoCamera;

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
	protected int idIndiagram;

	protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	protected Uri fileUri;

	protected boolean active = false;
	protected static final int CATEGORY_REQUEST = 101;
	public static Category category_request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_indiagram);
    	AppData.current_activity = this;

		// Fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Get TextView
		m_indiagramSound = (TextView) findViewById(R.id.m_indiagramSound);
		m_indiagramCategory = (TextView) findViewById(R.id.m_indiagramCategory);
		//  Create new Indiagram
		m_indiagram = new Indiagram();
		ScreenManager.voiceReader = this.m_voiceReader;

		m_parent = AppData.homeCategory;

		// Get Checkbox
		m_indiagramIsCategory = (CheckBox) findViewById(R.id.isCategory);
		m_indiagramIsCategory.setOnCheckedChangeListener(this);

		// Get EditText 
		m_indiagramText = (EditText) findViewById(R.id.edit_text);

		// Get Buttons
		Button buttonSound = (Button) findViewById(R.id.choose_sound);
		Button buttonListen = (Button) findViewById(R.id.listen);
		Button buttonOk = (Button) findViewById(R.id.okButton);
		Button buttonCancel = (Button) findViewById(R.id.cancelButton);
		Button buttonImage = (Button) findViewById(R.id.choose_picture);
		Button buttonCategoryPick = (Button) findViewById(R.id.chooseCategory);
		Button buttonHomeCategory = (Button) findViewById(R.id.racine);
		Button buttonDeleteSound = (Button) findViewById(R.id.deleteSound);

		// Listener buttons
		buttonSound.setOnClickListener(this);
		buttonListen.setOnClickListener(this);
		buttonOk.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		buttonImage.setOnClickListener(this);
		buttonCategoryPick.setOnClickListener(this);
		buttonHomeCategory.setOnClickListener(this);
		buttonDeleteSound.setOnClickListener(this);


		// Get Layout 
		m_categoryInfoLayout = (LinearLayout) findViewById(R.id.layout_color);

		m_indiagramTextColor = (LinearLayout) findViewById(R.id.m_indiagramTextColor);
		m_indiagramTextColor.setOnClickListener(this);

		// Get Image
		m_indiagramImage = (ImageView) findViewById(R.id.indiagram);
		init();
		refresh();
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
		refresh();
	}


	public void onPush(){
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

		try
		{
			m_indiagramImage.setImageBitmap(ImageManager.loadImage(PathData.IMAGE_DIRECTORY + m_indiagram.imagePath, AppData.settings.indiagramSize, AppData.settings.indiagramSize));
		} 
		catch (Exception e)
		{
			//Log.wtf("AddIndiagram", "Unable to load image", e);
		}

	}

	public void afficherPopupSelectrionPhoto(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		String[] sources = { getResources().getString(R.string.camera), getResources().getString(R.string.gallery) };

		builder.setTitle(R.string.choosePicture)
		.setItems(sources, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				switch(position){
				case 0:
					Log.e("AddIndiagram","Choose Picture");
					getPhotoFromCamera();
					break;
				case 1:
					getPhotoFromAlbum();
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}



	public void afficherPopupSelectrionSound(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		String[] sources = { getResources().getString(R.string.recordSound), getResources().getString(R.string.gallery) };

		builder.setTitle(R.string.chooseSound)
		.setItems(sources, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				switch(position){
				case 0:
					getSoundFromRecord();
					break;
				case 1:
					getSoundFromMedia();
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void getPhotoFromCamera(){
		
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		_photoCamera = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_photoCamera));
		startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
		 

	}

	public void getSoundFromRecord(){
		RecordSound record = new RecordSound(AddIndiagram.this);
		record.show();
		refresh();
	}

	public void getSoundFromMedia(){		
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		startActivityForResult(intent, SOUND_REQUEST);
	}

	public void getPhotoFromAlbum(){		
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, IMAGE_REQUEST);
	}

	protected void getPhoto(String filePath){
		if(filePath != null && !filePath.equals("")){
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(filePath,o);

			//The new size we want to scale to
			final int REQUIRED_WIDTH=300;
			final int REQUIRED_HIGHT=300;
			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;

			Bitmap photo = BitmapFactory.decodeFile(filePath,o2);
			photo = ImageResizer.applyOrientation(photo,new File(filePath));
			//Log.e("Photo null ?", (photo == null)+" "+photo.getHeight());

			System.out.println("photo");

			m_indiagramImage.setImageBitmap(photo);

			String path = PhotoHandler.onPictureTaken(photo);
			savePhoto(path);
			refresh();
		}
	}

	// By using this method get the Uri of Internal/External Storage for Media
	private Uri getUri() {
	    String state = Environment.getExternalStorageState();
	    if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
	        return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

	    return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{ 
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == Activity.RESULT_OK){  
			switch(requestCode){
			case CATEGORY_REQUEST:
				categoryChanged(category_request);
				refresh();
				break;
			case CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE:
				
				if(_photoCamera != null)
				getPhoto(_photoCamera.getAbsolutePath());

					
				break;

			case IMAGE_REQUEST:
/*
				Uri	selectedImage = data.getData();
				if(selectedImage != null){
					String[] filePathColumn = {MediaStore.Images.Media.DATA};

					Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					getPhoto(filePath);
				}
			 */
				Uri	selectedImage = data.getData();



				try {
					ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage, "r");
					   final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
					   final Bitmap photo = BitmapFactory.decodeFileDescriptor(fileDescriptor);
					   parcelFileDescriptor.close();
						//Log.e("Photo null ?", (photo == null)+" "+photo.getHeight());

						System.out.println("photo");

						m_indiagramImage.setImageBitmap(photo);

						String path = PhotoHandler.onPictureTaken(photo);
						savePhoto(path);
						refresh();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				break;
				
			case SOUND_REQUEST :
				Uri selectSound = data.getData();

				if(selectSound != null){

					String filePath = selectSound.getPath();
					if(filePath==null){
						return;
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
					String date = dateFormat.format(new Date());
					String  m_filePath = PathData.USER_SOUND_DIRECTORY + "Record_" + date + ".3gp";


					//recopie du fichier de son vers l'espace de stockage de son de l'indiagram
					InputStream inStream = null;
					OutputStream outStream = null;

					try{

						File output_file =new File(PathData.SOUND_DIRECTORY + m_filePath);



						inStream = new FileInputStream( selectSound.getPath().toString());
						outStream = new FileOutputStream(output_file);

						byte[] buffer = new byte[1024];

						int length;
						//copy the file content in bytes 
						while ((length = inStream.read(buffer)) > 0){

							outStream.write(buffer, 0, length);

						}

						inStream.close();
						outStream.close();

					}catch(IOException e){
						e.printStackTrace();
					}

					saveSound(m_filePath);
					refresh();
				}
				break;
			}

		}
	}

	boolean save;
	ProgressDialog mProgressDialog;

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		if(this instanceof EditIndiagram){
			AppData.current_indiagram  = m_indiagram;
			Intent intent = new Intent(this,ViewIndiagram.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(getApplicationContext(),CollectionManagement.class);
			startActivity(intent);
		}
		finish();

	}
	public void onClick(View _view)
	{
		switch (_view.getId()) {
		case R.id.cancelButton:
			if(this instanceof EditIndiagram){
				AppData.current_indiagram  = m_indiagram;
				Intent intent = new Intent(this,ViewIndiagram.class);
				startActivity(intent);
				finish();
			}else{
				Intent intent = new Intent(getApplicationContext(),CollectionManagement.class);
				startActivity(intent);
			}
			finish();
			break;
		case R.id.racine:
			m_parent = AppData.homeCategory;
			refresh();
			break;

		case R.id.listen:
			this.m_indiagram.text = this.m_indiagramText.getText().toString();

			if(!TextUtils.isEmpty(this.m_indiagramText.getText().toString()) ||
					!TextUtils.isEmpty(this.m_indiagram.soundPath)
					){
				ScreenManager.voiceReader.read(this.m_indiagram);
			}else{
				Toast.makeText(this,this.getResources().getString(R.string.notsound), Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.choose_picture:
			afficherPopupSelectrionPhoto();
			break;

		case R.id.choose_sound:
			afficherPopupSelectrionSound();
			break;

		case R.id.okButton:

			if(m_indiagramText.length() > 0){

				save = false;
				mProgressDialog = ProgressDialog.show(this,getString(R.string.please_wait),
						getString(R.string.long_operation), true);

				new Thread((new Runnable() {
					@Override
					public void run() {

						save = saveIndiagram();
						Message msg = mHandler.obtainMessage(2,getString(R.string.save_successfully));
						mHandler.sendMessage(msg);
					}
				})).start();


			}else{
				Toast.makeText(this, getResources().getString(R.string.error_add_indiagram_name), Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.chooseCategory:
			Intent intent = new Intent(this,CategoryBrowserActivity.class);
			startActivityForResult(intent, CATEGORY_REQUEST);

			break;
		case R.id.deleteSound :
			dialogDeleteSound();
			break;

		case R.id.m_indiagramTextColor:
			
		{
			if(!active){
				active = true;
			final ColorPickerDialog colorDialog = new ColorPickerDialog(this, m_categoryTextColor);

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.chooseColor));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active = false;
					m_categoryTextColor = colorDialog.getColor();
					m_indiagramTextColor.setBackgroundColor(m_categoryTextColor);
					refresh();
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					active = false;
					//Nothing to do here.
				}
			});

			colorDialog.show();
			}
		}
		
			break;
		default:
			break;
		}

	}


	public synchronized void dialogDeleteSound()
	{


		AlertDialog.Builder adb = new AlertDialog.Builder(this);

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
		adb.show();
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

	public void quitter(){
		if(this instanceof EditIndiagram){
			Intent intent = new Intent(this,ViewIndiagram.class);
			intent.putExtra("idIndia",ViewIndiagram.m_indiagram.getView().getId());
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(getApplicationContext(),CollectionManagement.class);
			startActivity(intent);
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_indiagram, menu);
		return true;
	}

	@SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:

				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					if(save){

						quitter();

					}
				}
				break;
			default: // should never happen
				break;
			}
		}
	};
}
