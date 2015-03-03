package org.indiarose.backend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.R;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.camera.PhotoHandler;
import org.indiarose.backend.view.element.AbstractScreen;
import org.indiarose.backend.view.element.ButtonItem;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Signal : 
 * 	- photoTaken(String _filename)
 * @author Julien
 *
 */

public class TakePhoto extends AbstractScreen
{
	protected static final String TAKE = "take";
	protected static final String CANCEL = "cancel";
	
	protected Camera m_camera = null;
	protected PhotoHandler m_callback = null;
	protected LinearLayout m_layout = null;
	
	protected SurfaceView previewSurface = null;
	protected SurfaceHolder previewHolder = null;
	protected boolean inPreview = false;
	protected boolean cameraConfigured = false;
	
	protected boolean m_takingPhoto = false;
	
	public TakePhoto(ScreenManager _screen)
	{
		super(_screen);
		
		m_callback = new PhotoHandler();
		previewSurface = new SurfaceView(AppData.currentContext);
		
		ButtonItem takeButton = new ButtonItem(AppData.currentContext, TAKE);
		ButtonItem cancelButton = new ButtonItem(AppData.currentContext, CANCEL);
		
		takeButton.setTextSize(ScreenManager.fontSize);
		cancelButton.setTextSize(ScreenManager.fontSize);
		takeButton.setTextColor(Color.BLACK);
		cancelButton.setTextColor(Color.BLACK);
		takeButton.setText(R.string.takeImage);
		cancelButton.setText(R.string.cancelText);
		
		try
		{
			Mapper.connect(m_callback, "pictureTaken", this, "savePhoto");
			Mapper.connect(takeButton, "clicked", this, "buttonEvent");
			Mapper.connect(cancelButton, "clicked", this, "buttonEvent");
		} 
		catch (MapperException e)
		{
			Log.wtf("TakePhoto", "unable to connect photo", e);
		}
		
		m_layout = new LinearLayout(AppData.currentContext);
		m_layout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout buttonLayout = new LinearLayout(AppData.currentContext);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.addView(cancelButton);
		buttonLayout.addView(takeButton);
		
		
		m_layout.setGravity(Gravity.CENTER);
		m_layout.addView(previewSurface, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		m_layout.addView(buttonLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		previewHolder = previewSurface.getHolder();
		previewHolder.addCallback(surfaceCallback);
	    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    
	    ViewGroup.LayoutParams p = previewSurface.getLayoutParams();
	    int h = 300;
		int w = (int)(300.0 * this.m_screen.getWidth() / this.m_screen.getHeight());
	    p.height = h;
	    p.width = w;
	    previewSurface.setLayoutParams(p);
	    
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback()
	{
		public void surfaceCreated(SurfaceHolder holder)
		{
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
			// no-op
		}
	};

	private void initPreview(int w, int h)
	{
		if (m_camera != null && previewHolder.getSurface() != null)
		{
			try
			{
				m_camera.setPreviewDisplay(previewHolder);
			} 
			catch (Throwable t)
			{
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(AppData.currentContext, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured)
			{
				Camera.Parameters parameters = m_camera.getParameters();

				
				
				parameters.setPreviewSize(w, h);
				parameters.setPictureSize(w, h);
				m_camera.setParameters(parameters);
				cameraConfigured = true;
			}
		}
	}

	private void startPreview()
	{
		if (cameraConfigured && m_camera != null)
		{
			m_camera.startPreview();
			inPreview = true;
		}
	}
	
	public void savePhoto(String _filename)
	{
		m_camera.stopPreview();
		try
		{
			Mapper.emit(this, "photoTaken", _filename);
		} 
		catch (MapperException e)
		{
			Log.wtf("TakePhoto", "unable to emit photo saving", e);
		}
		this.m_screen.pop();
	}
	
	public void onPop()
	{
		if(m_camera != null)
		{
			m_camera.release();
			m_camera = null;
		}
	}
	
	public void onPush()
	{
		m_camera = Camera.open();
	}
	
	@Override
	public View getView()
	{
		return m_layout;
	}

	public synchronized void buttonEvent(ButtonItem _button)
	{
		if(!m_takingPhoto)
		{
			String id = _button.getIdentifier();
			if(id.equals(CANCEL))
			{
				this.m_screen.pop();
			}
			else if(id.equals(TAKE))
			{
				m_takingPhoto = true;
				this.m_camera.takePicture(null, null, this.m_callback);
			}
		}
	}	
}
