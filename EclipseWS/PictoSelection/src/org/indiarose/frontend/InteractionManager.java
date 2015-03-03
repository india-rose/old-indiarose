package org.indiarose.frontend;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;
import java.util.List;

import org.indiarose.R;
import org.indiarose.frontend.view.IndiagramBrowser;
import org.indiarose.frontend.view.SentenceArea;
import org.indiarose.frontend.view.TitleBar;
import org.indiarose.lib.ActionLog;
import org.indiarose.lib.AppData;
import org.indiarose.lib.IndiaLogger;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.view.EventResult;
import org.indiarose.lib.view.IndiagramView;
import org.indiarose.lib.voice.VoiceReader;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class InteractionManager
{
	protected TitleBar m_title = null;
	protected IndiagramBrowser m_browser = null;
	protected SentenceArea m_sentence = null;
	protected List<Indiagram> lastList = null;
	protected VoiceReader m_voice = null;
	protected AbsoluteLayout m_windowLayout = null;

	protected boolean m_interfaceEnabled = true;
	protected boolean m_isReading = false;
	protected boolean m_isMoving = false;
	protected boolean m_inCorrection = false;

	protected int m_ySeparationLine = 0;
	protected DisplayMetrics m_windowSize = null;

	public InteractionManager(TitleBar _title, IndiagramBrowser _browser, SentenceArea _sentence, AbsoluteLayout _windowLayout, VoiceReader _reader, int _yLine, DisplayMetrics _windowSize)
	{
		this.m_title = _title;
		this.m_browser = _browser;
		this.m_sentence = _sentence;
		this.m_voice = _reader;
		this.m_windowLayout = _windowLayout;
		this.m_ySeparationLine = _yLine;
		this.m_windowSize = _windowSize;

		//connect signal / slot

		try
		{
			// change the title bar text and image when the category change
			Mapper.connect(this.m_browser, "categoryChanged", this.m_title, "setTitleInfo");

			// manage indiagram moving from & to indiagram browser
			Mapper.connect(this.m_sentence, "indiagramAdded", this.m_browser, "indiagramPositionChanged");
			Mapper.connect(this.m_sentence, "indiagramRemoved", this.m_browser, "indiagramPositionChanged");

			// manage enable/disable user input in reading mode
			Mapper.connect(this.m_sentence, "startReading", this, "disableInteraction");
			Mapper.connect(this.m_sentence, "completeReading", this, "enableInteraction");

			// manage clean and return home category when sentence read
			Mapper.connect(this.m_sentence, "completeReading", this, "resetAll");

			// manage user input
			Mapper.connect(this.m_browser, "indiagramEvent", this, "indiagramBrowserEvent");
			Mapper.connect(this.m_browser, "nextButtonTouched", this, "nextButtonTouched");

			Mapper.connect(this.m_sentence, "indiagramEvent", this, "indiagramSentenceEvent");
			Mapper.connect(this.m_sentence, "layoutEvent", this, "layoutSentenceEvent");
			Mapper.connect(this.m_sentence, "playButtonEvent", this, "playButtonEvent");
		}
		catch(MapperException e)
		{
			Log.wtf("InputManager", e);
		}
	}

	public void disableInteraction()
	{
		synchronized(this)
		{
			Log.e("Read", "disableInteraction");
			this.m_interfaceEnabled = false;
		}
	}

	public void enableInteraction()
	{
		synchronized(this)
		{
			Log.e("Read", "enableInteraction");
			this.m_interfaceEnabled = true;
		}
	}

	public void resetAll()
	{
		Log.e("R", "resetAll");
		this.m_browser.setVisibleNext(true);
		this.m_sentence.removeAll();
		this.m_browser.reset();
	}

	public void indiagramBrowserEvent(IndiagramView _view, MotionEvent _event, EventResult _result)
	{
		synchronized(this)
		{
			Log.e("Interaction Manager", " Debut fonction indiagramBrowserEvent");

			if(this.m_interfaceEnabled)
			{
				if(_event.getActionMasked() == MotionEvent.ACTION_DOWN)
				{
					
					Log.e("Interaction Manager","ACTION_DOWN");
					if(_view.getIndiagram() instanceof Category)
					{
						IndiaLogger.addActionLog(AppData.currentContext,""+_view.getIndiagram().text, ActionLog.TYPE_CATEGORY, System.currentTimeMillis());
						if(AppData.settings.enableCategoryReading)
						{
							this.readIndiagram(_view.getIndiagram());
						}

						this.m_browser.pushCategory((Category)_view.getIndiagram());						
						_result.eventResult = false;
					}
					else
					{
						if(AppData.settings.enableDragAndDrop)
						{
							if(AppData.settings.enableSelectionReinforcer)
							{
								this.readIndiagram(_view.getIndiagram());
							}
							_view.m_isInDragAndDrop = true;

							Log.e("Interaction Manager","Remove Indiagram");
							this.m_browser.removeIndiagram(_view);

							this.m_browser.indiagramPositionChanged(_view);
							Mapper.disconnect(_view);
							try
							{
								Mapper.connect(_view, "touchEvent", this, "indiagramDragAndDropInteraction");
							} 
							catch (MapperException e)
							{
								Log.wtf("InteractionManager", "Mapper connection failed", e);
							}

							this.m_isMoving = true;
							this.m_windowLayout.addView(_view, new AbsoluteLayout.LayoutParams(_view.getRealHeight(), IndiagramView.getDefaultWidth(), (int)_event.getRawX() - _view.getRealHeight() / 2, (int)_event.getRawY() - IndiagramView.getDefaultWidth() / 2));
							_result.eventResult = true;
						}
						else
						{
							if(this.m_sentence.canAddIndiagram())
							{
								if(AppData.settings.enableSelectionReinforcer)
								{

									this.readIndiagram(_view.getIndiagram());
								}
								Log.e("Interaction Manager", "J'appuie sur l'indiagram on va le remove");

								//Mapper.disconnect(_view);
								this.m_browser.removeIndiagram(_view);
								this.m_sentence.add(_view);
								IndiaLogger.addActionLog(AppData.currentContext,""+_view.getIndiagram().text, ActionLog.TYPE_INDIAGRAM, System.currentTimeMillis());
								
							}
							_result.eventResult = true;
						}
					}
				}
			}
			
			Log.e("Interaction Manager", " Fin fonction indiagramBrowserEvent");

		}
	}

	public void indiagramDragAndDropInteraction(IndiagramView _view, MotionEvent _event, EventResult _result)
	{
		synchronized(this)
		{
			if(this.m_isMoving)
			{
				Log.d("InteractionManager","Interaction drag and drop ");
				_result.eventResult = true;
				if(_event.getActionMasked() == MotionEvent.ACTION_MOVE)
				{
					_view.setLayoutParams(new AbsoluteLayout.LayoutParams(_view.getRealHeight(), IndiagramView.getDefaultWidth(), (int)_event.getRawX() - _view.getRealHeight() / 2, (int)_event.getRawY() - IndiagramView.getDefaultWidth() / 2));
				}
				else if(_event.getActionMasked() == MotionEvent.ACTION_UP)
				{
					Mapper.disconnect(_view);
					_view.m_isInDragAndDrop = false;
					this.m_isMoving = false;
					this.m_windowLayout.removeView(_view);
					_view.setLayoutParams(new LayoutParams(IndiagramView.getDefaultWidth(), _view.getRealHeight()));

					if(_event.getRawY() > this.m_ySeparationLine)
					{

						this.m_sentence.add(_view);
						IndiaLogger.addActionLog(AppData.currentContext,_view.getIndiagram().text, ActionLog.TYPE_INDIAGRAM, System.currentTimeMillis());

					}
					else
					{
						Log.d("InteractionManager","Je le laisse du m��me c��t��");
						this.m_browser.indiagramPositionChanged(_view);
					}
				}
			}
		}
	}	

	protected void readIndiagram(Indiagram _indiagram)
	{
		this.m_interfaceEnabled = false;
		this.m_isReading = true;

		try
		{
			Mapper.connect(this.m_voice, "readingComplete", this, "readingComplete");
		} 
		catch (MapperException e)
		{
			Log.wtf("InteractionManager", "readIndiagram", e);
		}

		this.m_voice.read(_indiagram);
	}

	public void readingComplete(Indiagram _indiagram)
	{
		if(this.m_isReading)
		{
			synchronized(this)
			{
				if(this.m_isReading)
				{
					this.m_isReading = false;
					this.m_interfaceEnabled = true;
					Mapper.disconnect(this.m_voice, "readingComplete", this, "readingComplete");
				}
			}
		}
	}

	public void indiagramSentenceEvent(IndiagramView _view, MotionEvent _event, EventResult _result)
	{
		synchronized(this)
		{
			if(this.m_interfaceEnabled)
			{
				if(_event.getActionMasked() == MotionEvent.ACTION_DOWN)
				{
					IndiaLogger.addActionLog(AppData.currentContext,""+_view.getIndiagram().text, ActionLog.TYPE_SUPPRESSION, System.currentTimeMillis());
					this.m_sentence.remove(_view);
				}
			}
		}
	}

	public void layoutSentenceEvent(View _view, MotionEvent _event, EventResult _result)
	{
		synchronized(this)
		{
			if(this.m_interfaceEnabled)
			{
				Log.i("InputManager", "Event detected from sentence layout : " + _event.getActionMasked());
				_result.eventResult = true;
			}
		}
	}

	public void nextButtonTouched(IndiagramView _view, EventResult _result)
	{
		synchronized(this)
		{
			if(this.m_interfaceEnabled)
			{
				if(!this.m_browser.isMoreView())
				{
					this.m_browser.popCategory();
				}
				else
				{
					this.m_browser.nextIndiagram();
				}
			}
		}
		IndiaLogger.addActionLog(AppData.currentContext,"next", ActionLog.TYPE_NEXT, System.currentTimeMillis());
	}

	public void playButtonEvent(IndiagramView _view, MotionEvent _event, EventResult _result)
	{
		synchronized(this)
		{
			if(this.m_interfaceEnabled)
			{
				if(_event.getActionMasked() == MotionEvent.ACTION_DOWN)
				{
					if(this.m_inCorrection)
					{
						//TODO Apr��s correction
//						IndiaLogger.writeCorrections(lastList, this.m_sentence.getIndiagramsList());
//						//la liste d'avant
//						for(Indiagram i : lastList){
//							IndiaLogger.addActionLog(i.text, ActionLog.TYPE_INDIAGRAM, System.currentTimeMillis());
//						}
						IndiaLogger.addActionLog(AppData.currentContext,"correction", ActionLog.TYPE_CORRECTION, System.currentTimeMillis()); 	
						for(Indiagram i : this.m_sentence.getIndiagramsList()){
							IndiaLogger.addActionLog(AppData.currentContext,i.text, ActionLog.TYPE_INDIAGRAM, System.currentTimeMillis());
						}
						IndiaLogger.addActionLog(AppData.currentContext,"validation", ActionLog.TYPE_VALIDER, System.currentTimeMillis());
						this.m_sentence.read();
						this.m_inCorrection = false;
						_result.eventResult = false;
					}
					else
					{
						_result.eventResult = true;
					}
				}
				else if(_event.getActionMasked() == MotionEvent.ACTION_UP)
				{
					if(_event.getRawY() > this.m_ySeparationLine && _event.getRawX() < this.m_windowSize.widthPixels / 3)
					{
						/*
						 * Hide next button
						 * Create category Correction with string lang dependent
						 * Add all indiagram into the sentence area to this correction category
						 * Add the category to indiagram browser
						 * disable to correction mode
						 */
						ArrayList<Indiagram> indiagrams = this.m_sentence.getIndiagramsList();
						if(indiagrams.size() > 0)
						{
							this.m_inCorrection = true;
							Category correction = new Category(AppData.currentContext.getString(R.string.correctionCategoryName), "../app/correction.png", "", Color.WHITE);
							correction.indiagrams.addAll(indiagrams);
							//TODO Faire le Log Correction Phrase avant

							// DO NOT RESET BROWSER !! IT CONDUCT IN PUSH CATEGORY SYNCHRONIZATION PROBLEM
							this.lastList = new ArrayList<Indiagram>();
							this.lastList.addAll(m_sentence.getIndiagramsList());
							this.m_sentence.removeAll();

							this.m_browser.setVisibleNext(false);
							this.m_browser.pushCategory(correction);
						}
					}
					else
					{
						// TODO phrase
//						IndiaLogger.writeAllSentence(this.m_sentence.getIndiagramsList());
//						for(Indiagram i : this.m_sentence.getIndiagramsList()){
//							IndiaLogger.addActionLog(i.text, ActionLog.TYPE_INDIAGRAM, System.currentTimeMillis());
//						}
						IndiaLogger.addActionLog(AppData.currentContext,"validation", ActionLog.TYPE_VALIDER, System.currentTimeMillis());
						this.m_sentence.read();
						
					}
				}
			}
		}
	}

}
