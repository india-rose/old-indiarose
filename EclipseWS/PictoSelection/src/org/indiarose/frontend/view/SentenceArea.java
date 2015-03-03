package org.indiarose.frontend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.*;

import org.indiarose.lib.AppData;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.view.EventResult;
import org.indiarose.lib.view.IndiagramView;
import org.indiarose.lib.voice.VoiceReader;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.graphics.Color;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

/**
 * This class manage the bottom part of the window where sentence are made.
 * Available signal : - indiagramRemoved (IndiagramView) : raised when an
 * indiagram is removed from the sentence. - indiagramAdded (IndiagramView) :
 * raised when an indiagram is added to the sentence. - playButtonEvent
 * (IndiagramView, MotionEvent, EventResult) : raised when the play button is
 * touched down. - indiagramEvent (IndiagramView, MotionEvent, EventResult) :
 * raised when an indiagram raise a touch event. - layoutEvent (View,
 * MotionEvent, EventResult) : raised when the layout raise a touch event. -
 * startReading (void) : raised when the reading process is started. -
 * completeReading (void) : raised when the sentence reading is over.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class SentenceArea implements OnTouchListener {
	/**
	 * Layout where to add all view of the sentence.
	 */
	protected RelativeLayout m_layout = null;

	/**
	 * List of views actually in the sentence.
	 */
	protected ArrayList<IndiagramView> m_views = new ArrayList<IndiagramView>();
	/**
	 * Handler to manage thread creator only operation.
	 */
	protected SentenceAreaHandler m_handler = null;

	/**
	 * Id of the next view.
	 */
	protected int m_id = 0x2A;
	/**
	 * Number of indiagram which can be add in the sentence.
	 */
	protected int m_numberOfIndiagram = 0;

	// reading sentence relative variable
	protected VoiceReader m_voiceEngine;
	protected int m_readingIndex = 0;
	protected boolean m_isReading = false;
	protected Timer m_delayReadingTimer = new Timer();

	/**
	 * Method to construct the area manager.
	 * 
	 * @param _layout
	 *            : the layout to use to add view.
	 * @param _width
	 *            : the available width.
	 * @param _voiceEngine
	 *            : the voice engine to use to read sentence.
	 */
	public SentenceArea(RelativeLayout _layout, int _width,
			VoiceReader _voiceEngine) {
		this.m_layout = _layout;
		this.m_handler = new SentenceAreaHandler(this);

		this.m_numberOfIndiagram = _width / IndiagramView.getDefaultWidth() - 1;
		this.m_voiceEngine = _voiceEngine;

		this.m_layout.setOnTouchListener(this);

		// Init play button
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);

		try {
			IndiagramView view = AppData.playButtonIndiagram.getView();
			this.m_layout.addView(view, lp);

			Mapper.connect(view, "touchEvent", this, "playButtonEvent");
		} catch (MapperException e) {
			Log.wtf("IndiagramBrowser", e);
		}
	}

	/**
	 * Method to check if more indiagram can be added.
	 * 
	 * @return true if you can add more indiagram.
	 */
	public boolean canAddIndiagram() {
		return (this.m_views.size() < this.m_numberOfIndiagram && !isReading());
	}

	/**
	 * Method to check if the sentence is currently read.
	 * 
	 * @return true if reading is under process.
	 */
	public boolean isReading() {
		return this.m_isReading;
	}

	/**
	 * Method to add an indiagram to the sentence area.
	 * 
	 * @param _view
	 *            : the indiagram view to add.
	 * @return true on success.
	 */
	public boolean add(IndiagramView _view) {
		if (canAddIndiagram()) {
			synchronized (this) {
				_view.setId(this.m_id++);
				this.m_views.add(_view);
				this.m_handler
						.sendEmptyMessage(SentenceAreaHandler.REFRESH_MESSAGE);
			}
			try {
				Mapper.emit(this, "indiagramAdded", _view);
				Mapper.connect(_view, "touchEvent", this, "indiagramEvent");
			} catch (MapperException e) {
				Log.wtf("PhraseArea", e);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method to remove a view from the sentence area. This method is thread
	 * safe.
	 * 
	 * @param _view
	 *            : the view to remove.
	 */
	public void remove(IndiagramView _view) {
		if (!isReading() && this.m_views.size() > 0) {
			removeIndiagram(_view);
		}
	}

	/**
	 * Method to remove all indiagram from the sentence area.
	 */
	public void removeAll() {
		if (!isReading() && this.m_views.size() > 0) {
			this.m_handler.sendEmptyMessage(SentenceAreaHandler.RESET_MESSAGE);
		}
	}

	/**
	 * Method to remove all indiagram from the sentence area. This method must
	 * only be called by a handler of this object.
	 */
	protected void removeAllHandler() {
		IndiagramView[] views = this.m_views.toArray(new IndiagramView[] {});
		for (IndiagramView view : views) {
			removeIndiagram(view);
		}

		this.m_views.clear();
		this.m_id = 0x2A;
	}

	/**
	 * Method to remove one view from the sentence area. This method is not
	 * thread safe and must be protected as necessary.
	 * 
	 * @param _view
	 *            : the view to remove.
	 */
	protected void removeIndiagram(IndiagramView _view) {
		synchronized (this) {
			this.m_layout.removeView(_view);
			this.m_views.remove(_view);

			this.m_handler
					.sendEmptyMessage(SentenceAreaHandler.REFRESH_MESSAGE);
		}

		try {
			Mapper.disconnect(_view);
			Mapper.emit(this, "indiagramRemoved", _view);
		} catch (MapperException e) {
			Log.wtf("PhraseArea", e);
		}
	}

	/**
	 * Method to check if an indiagram is in the sentence area.
	 * 
	 * @param _item
	 *            : the indiagram you are looking for.
	 * @return true if present, false otherwise.
	 */
	public boolean hasIndiagram(Indiagram _item) {
		synchronized (this) {
			for (int i = 0; i < this.m_views.size(); ++i) {
				if (this.m_views.get(i).getIndiagram().equals(_item)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Method to retrieve the collection of all indiagrams actually in the
	 * sentence.
	 * 
	 * @return the list of indiagrams in the sentence.
	 */
	public ArrayList<Indiagram> getIndiagramsList() {
		synchronized (this) {
			ArrayList<Indiagram> result = new ArrayList<Indiagram>();
			for (int i = 0; i < this.m_views.size(); ++i) {
				result.add(this.m_views.get(i).getIndiagram());
			}
			return result;
		}
	}

	/**
	 * Method to refresh indiagram position and redisplay the area. This method
	 * is not thread safe.
	 */
	protected void refreshLayout() {
		Log.e("Sentence Area", "Je raffraichi");

		// TODO Normalement Bon
		for (int i = 0; i < this.m_views.size(); ++i) {
			this.m_layout.removeView(this.m_views.get(i));
		}

		for (int i = 0; i < this.m_views.size(); ++i) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_VERTICAL);

			if (i > 0) {
				lp.addRule(RelativeLayout.RIGHT_OF, this.m_views.get(i - 1)
						.getId());
			} else {
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}

			this.m_layout.addView(this.m_views.get(i), lp);
		}
	}

	/**
	 * Method called when the play button is touched.
	 * 
	 * @param _view
	 *            : the play button view.
	 * @param _event
	 *            : the event raised.
	 * @param _result
	 *            : the result to transmit to the event.
	 */
	public synchronized void playButtonEvent(IndiagramView _view,
			MotionEvent _event, EventResult _result) {
		try {
			Mapper.emit(this, "playButtonEvent", _view, _event, _result);
		} catch (MapperException e) {
			Log.wtf("SentenceArea", e);
		}
	}

	/**
	 * Method called when one of the indiagram is touched.
	 * 
	 * @param _view
	 *            : the indiagram view which raise the event.
	 * @param _event
	 *            : the event information.
	 * @param _result
	 *            : the result to transmit to the event.
	 */
	public synchronized void indiagramEvent(IndiagramView _view,
			MotionEvent _event, EventResult _result) {
		try {
			Mapper.emit(this, "indiagramEvent", _view, _event, _result);
		} catch (MapperException e) {
			Log.wtf("SentenceArea", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	public synchronized boolean onTouch(View _view, MotionEvent _event) {
		Log.wtf("SentenceArea", "onTouch layout !!");
		try {
			EventResult result = new EventResult();
			Mapper.emit(this, "layoutEvent", _view, _event, result);
			return result.eventResult;
		} catch (MapperException e) {
			Log.wtf("SentenceArea", e);
		}
		return false;
	}

	/**
	 * Method to launch the reading sentence process.
	 */
	public void read() {
		if (!this.m_isReading && this.m_views.size() > 0) {
			synchronized (this) {
				// if the reading process is not already launch and there is at
				// least one indiagram in the sentence.
				if (!this.m_isReading && this.m_views.size() > 0) {
					this.m_readingIndex = 0;
					this.m_isReading = true;

					try {
						Mapper.emit(this, "startReading");
						Mapper.connect(this.m_voiceEngine, "readingComplete",
								this, "endReading");
					} catch (MapperException e) {
						Log.wtf("SentenceArea", e);
					}

					readSentence();
				}
			}
		}
	}

	/**
	 * Callback method for timer to read all the sentence one indiagram by one.
	 */
	protected void readSentence() {
		Log.e("Read", "readSentence");
		if (this.m_isReading) {
			// if there is more view to read.
			if (this.m_readingIndex < this.m_views.size()) {
				if (this.m_readingIndex > 0
						&& AppData.settings.enableReadingReinforcer) {
					// disable reinforcer background on the last read indiagram.
					this.m_views.get(this.m_readingIndex - 1)
							.setIndiagramBackground(Color.TRANSPARENT);
				}
				IndiagramView v = this.m_views.get(this.m_readingIndex);
				if (AppData.settings.enableReadingReinforcer) {
					v.setIndiagramBackground(AppData.settings.backgroundReinforcerReading);
				}
				this.m_voiceEngine.read(v.getIndiagram());
			} else {
				if (this.m_views.size() > 0) {
					this.m_views.get(this.m_views.size() - 1)
							.setIndiagramBackground(Color.TRANSPARENT);
				}
				this.m_isReading = false;
				try {
					Mapper.disconnect(this.m_voiceEngine, "readingComplete",
							this, "endReading");
					Mapper.emit(this, "completeReading");
				} catch (MapperException e) {
					Log.wtf("SentenceArea", e);
				}
			}
		}
	}

	/**
	 * Method to launch delay timer after word read.
	 * 
	 * @param _indiagram
	 *            : the last indiagram read.
	 */
	public void endReading(Indiagram _indiagram) {
		long _value;
		if ((long) AppData.settings.wordsReadingDelay < (long) 0.6) {
			_value = (long) (0.6 * 1000);
		} else {
			_value = (long) AppData.settings.wordsReadingDelay * 1000;
		}
		if (this.m_isReading) {
			this.m_delayReadingTimer.cancel();
			this.m_delayReadingTimer.purge();
			this.m_delayReadingTimer = new Timer();
			this.m_delayReadingTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					m_readingIndex++;
					readSentence();
				}
			}, _value);
		}
	}
}
