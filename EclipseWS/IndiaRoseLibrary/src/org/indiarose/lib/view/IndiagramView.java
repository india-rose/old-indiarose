package org.indiarose.lib.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import org.indiarose.lib.AppData;
import org.indiarose.lib.PathData;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.utils.FontManager;
import org.indiarose.lib.utils.ImageManager;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * This class manage Indiagram display.
 * 
 * This class used storm.communication.Mapper to raise signal.
 * These signals are : 
 * 		- touchEvent (IndiagramView, MotionEvent, EventResult) : signal raised when a touch event happened.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class IndiagramView extends View implements OnTouchListener
{
	/**
	 * Paint object to draw the image.
	 */
	protected Paint m_picturePainter = new Paint();
	/**
	 * Paint object to write the text.
	 */
	protected Paint m_textPainter = new Paint();
	/**
	 * Paint object to draw background.
	 */
	protected Paint m_backgroundPainter = new Paint();
	/**
	 * Rect of the view to draw background.
	 */
	protected Rect m_backgroundRect = null;
	
	/**
	 * Indiagram relative to this view.
	 */
	protected Indiagram m_indiagram = null;
	/**
	 * Handler of the view.
	 * Manage some view operation from other threads.
	 */
	protected IndiagramViewHandler m_handler = null;
	
	/**
	 * Current width of the view.
	 */
	protected int m_width = 0;
	/**
	 * Current height of the view.
	 */
	protected int m_height = 0;
	/**
	 * width of the image.
	 */
	protected int m_pictureWidth = 0;
	/**
	 * height of the image.
	 */
	protected int m_pictureHeight = 0;
	/**
	 * Left margin from left of the view to the image left edge.
	 */
	protected int m_marginLeft = 0;
	/**
	 * Top margin from top of the view to the image top edge.
	 */
	protected int m_marginTop = 0;
	/**
	 * Color of the text.
	 */
	protected int m_textColor = 0;
	/**
	 * Boolean to know if the text fill on one line or more.
	 */
	protected boolean m_isOneLineText = true;
	/**
	 * Boolean to know if the view is currently in drag and drop mode or not.
	 */
	public boolean m_isInDragAndDrop = false;
	
	/**
	 * Create an IndiagramView with the desired context.
	 * @param _context : context for the super View object.
	 */
	public IndiagramView(Context _context)
	{
		super(_context);
		
		this.m_handler = new IndiagramViewHandler(this);
		this.setOnTouchListener(this);
		
		this.m_picturePainter.setColor(Color.RED);
		this.m_textPainter.setTypeface(FontManager.loadFont(AppData.settings.fontFamily));
		this.m_textPainter.setColor(this.m_textColor);
		this.m_textPainter.setTextSize(AppData.settings.fontSize);
		this.m_textPainter.setTextAlign(Align.CENTER);
		this.m_backgroundPainter.setColor(Color.TRANSPARENT);
		
		this.m_pictureWidth = this.m_pictureHeight = AppData.settings.indiagramSize;
	}
	
	/**
	 * Change the indiagram of the view.
	 * @param _indiagram : the new indiagram to associate to the view.
	 */
	public synchronized void setIndiagram(Indiagram _indiagram)
	{				
		this.m_indiagram = _indiagram;
		refreshDimension();
		this.m_handler.sendEmptyMessage(IndiagramViewHandler.REFRESH_VIEW);
	}
	
	/**
	 * Change the color of the text.
	 * @param _color : the hex color code.
	 */
	public synchronized void setTextColor(int _color)
	{
		this.m_textColor = _color;
		this.m_textPainter.setColor(this.m_textColor);
		this.m_handler.sendEmptyMessage(IndiagramViewHandler.REFRESH_VIEW);
	}
	
	/**
	 * Change the color of the background.
	 * @param _color : the color integer.
	 */
	public synchronized void setIndiagramBackground(int _color)
	{
		this.m_backgroundPainter.setColor(_color);
		this.m_handler.sendEmptyMessage(IndiagramViewHandler.REFRESH_VIEW);
	}
	
	/**
	 * Change the dimension of the picture in the indiagram.
	 * @param _width : the new width.
	 * @param _height : the new height.
	 */
	public synchronized void setDimension(int _width, int _height)
	{
		this.m_pictureWidth = _width;
		this.m_pictureHeight = _height;
		refreshDimension();
		this.m_handler.sendEmptyMessage(IndiagramViewHandler.REFRESH_VIEW);
	}
	
	/**
	 * Get the indiagram relative to this view.
	 * @return the indiagram associated to the view.
	 */
	public Indiagram getIndiagram()
	{
		return this.m_indiagram;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	public synchronized boolean onTouch(View _v, MotionEvent _event)
	{
		synchronized(IndiagramView.class)
		{
			try
			{
				EventResult result = new EventResult();
				Mapper.emit(this, "touchEvent", this, _event, result);
				return true;
				//TODO debug nullpointer exception when returning false
				//TODO use this for normal return : "return result.eventResult;"
			}
			catch(MapperException e)
			{
				Log.wtf("IndiagramView", "indiagramView onTouch", e);
			}
		}
		return false;
	}
	
	/**
	 * Method to recalculate all dimension.
	 */
	protected void refreshDimension()
	{	
		float textWidth = this.m_textPainter.measureText(this.m_indiagram.text);
		int textHeight = AppData.settings.fontSize;
		
		if(TextUtils.isEmpty(this.m_indiagram.text))
		{
			textHeight = 0;
		}
		
		this.m_marginLeft = this.m_pictureWidth / 10;
		this.m_marginTop = this.m_pictureHeight / 10;
		
		this.m_width = this.m_marginLeft * 2 + this.m_pictureWidth;
		//height take account the fact that some words will needs two or more lines to be displayed.
		this.m_height = (int)(this.m_marginTop * 2 + this.m_pictureHeight + (textHeight * (int)(textWidth / (this.m_pictureWidth + 1) + 1)));
		
		this.setMinimumWidth(this.m_width);
		this.setMinimumHeight(this.m_height);
		this.measure(this.m_width, this.m_height);
		
		this.m_isOneLineText = (textWidth <= this.m_pictureWidth);
		this.m_backgroundRect = new Rect(0, 0, this.m_width, this.m_height);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		this.setMeasuredDimension(this.m_width, this.m_height);     
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas _canvas)
	{
		_canvas.drawRect(this.m_backgroundRect, this.m_backgroundPainter);
		
		//draw picture or a red rectangle if error with picture
		try
		{
			Bitmap image = ImageManager.loadImage(PathData.IMAGE_DIRECTORY + this.m_indiagram.imagePath, this.m_pictureWidth, this.m_pictureHeight);
			_canvas.drawBitmap(image, this.m_marginLeft, this.m_marginTop, this.m_picturePainter);
		}
		catch(Exception ex)
		{
			_canvas.drawRect(this.m_marginLeft, this.m_marginTop, this.m_pictureWidth + this.m_marginLeft, this.m_pictureHeight + this.m_marginTop, this.m_picturePainter);
		}
		
		if(!TextUtils.isEmpty(this.m_indiagram.text))
		{
			//write text
			int yindex = this.m_marginTop + this.m_pictureHeight + AppData.settings.fontSize;
			int xindex = this.m_marginLeft + this.m_pictureWidth / 2;
			String text = this.m_indiagram.text;

			if(this.m_isOneLineText)
			{
				_canvas.drawText(text, xindex, yindex, this.m_textPainter);
			}
			else
			{
				int txtOffset = 0;
				String text2 = null;
				
				while(txtOffset < text.length())
				{
					int textSize = this.m_textPainter.breakText(text, txtOffset, text.length(), true, this.m_pictureWidth, null);
					text2 = text.substring(txtOffset, textSize + txtOffset);
					
					_canvas.drawText(text2, xindex, yindex, this.m_textPainter);
					
					yindex += AppData.settings.fontSize;
					txtOffset += textSize;
				}
			}
		}
	}
	
	/**
	 * Method to get the real height of the view.
	 * View.getHeight() could not be accurate before one drawing.
	 * @return the real height of the view.
	 */
	public int getRealHeight()
	{
		return this.m_height;
	}
	
	/*
	 **************************
	 * Static utils function  *
	 **************************
	 */
	
	/**
	 * Method to retrieve the default width of an IndiagramView based on Settings in AppData.
	 * @return the width of an IndiagramView.
	 */
	public static int getDefaultWidth()
	{
		return AppData.settings.indiagramSize / 10 * 2 + AppData.settings.indiagramSize;
	}
	
	/**
	 * Method to retrieve the default height of an IndiagramView based on Settings in AppData.
	 * This function consider that text of this IndiagramView will fill on one line.
	 * @return the width of an IndiagramView.
	 */
	public static int getDefaultHeight()
	{
		return AppData.settings.indiagramSize / 10 * 2 + AppData.settings.indiagramSize + AppData.settings.fontSize;
	}
}


