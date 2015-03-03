package org.indiarose.frontend.view;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import org.indiarose.lib.AppData;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.view.EventResult;
import org.indiarose.lib.view.IndiagramView;

import storm.communication.Mapper;
import storm.communication.MapperException;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Class to manage indiagram browser area.
 * Available signal : 
 * 		- nextButtonTouched (IndiagramView, EventResult) : signal raised when the next button is touched down.
 * 		- indiagramEvent (IndiagramView, MotionEvent, EventResult) : signal raised when an event happened on an indiagram.
 * 		- categoryChanged(Category) : signal raised when the displayed category changed. The argument is the new one.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class IndiagramBrowser
{
	protected Stack<Category> m_categoriesStack = new Stack<Category>();
	
	protected RelativeLayout m_layout = null;
	protected SentenceArea m_sentence = null;
	protected boolean m_nextVisibleState = true;

	protected IndiagramBrowserHandler m_handler = null;
	
	protected int m_height = 0;
	
	protected ArrayList<IndiagramView> m_currentViews = new ArrayList<IndiagramView>();
	protected IndiagramView [][] m_displayableView = null;
	protected int m_lastOffset = 0;
	protected int m_currentViewOffset = 0;
	
	public IndiagramBrowser(RelativeLayout _layout, int _width, int _height, SentenceArea _sentence)
	{
		this.m_layout = _layout;
		this.m_height = _height;
		this.m_sentence = _sentence;
		
		this.m_handler = new IndiagramBrowserHandler(this);
		
		//calc how many view in a line			
		int indiagramByLine = _width / IndiagramView.getDefaultWidth();
		int numberOfLine = this.m_height / IndiagramView.getDefaultHeight();
		
		m_displayableView = new IndiagramView[numberOfLine][];
		for(int i = 0 ; i < numberOfLine ; ++i)
		{
			if(i == 0)
			{
				// save the space for next button
				m_displayableView[i] = new IndiagramView[indiagramByLine-1];
			}
			else
			{
				m_displayableView[i] = new IndiagramView[indiagramByLine];
			}
		}
		
		//init next button
		IndiagramView view = AppData.nextButtonIndiagram.getView();		
		try
		{
			Mapper.connect(view, "touchEvent", this, "nextButtonEvent");
		} 
		catch (MapperException e)
		{
			Log.wtf("IndiagramBrowser", e);
		}
	}
	
	public void reset()
	{
		this.m_handler.sendEmptyMessage(IndiagramBrowserHandler.RESET_MESSAGE);
	}
	
	public void resetAsync()
	{
		Category c = null;
		synchronized(this)
		{
			if(this.m_categoriesStack.size() > 0)
			{
				c = this.m_categoriesStack.get(0);
			}
			this.m_categoriesStack.clear();
			this.m_layout.removeAllViews();
			clearViewList();
		}
		if(c != null)
		{
			pushCategory(c);
		}
	}
	
	public void setVisibleNext(boolean _state)
	{
		boolean different = (this.m_nextVisibleState != _state);
		this.m_nextVisibleState = _state;
		if(different)
		{
			this.m_handler.sendEmptyMessage(IndiagramBrowserHandler.NEXT_BUTTON_MESSAGE);
		}
	}
	
	public void refreshNextButtonAsync()
	{
		synchronized(this)
		{
			this.m_currentViewOffset = this.m_lastOffset;
		}
		nextIndiagram();
	}
	
	protected void clearViewList()
	{
		for(int i = 0 ; i < this.m_currentViews.size() ; ++i)
		{
			if(!this.m_currentViews.get(i).m_isInDragAndDrop && !this.m_sentence.hasIndiagram(this.m_currentViews.get(i).getIndiagram()))
			{
				disconnectIndiagram(this.m_currentViews.get(i));
			}
		}
		this.m_currentViews.clear();
	}
	
	public void popCategory()
	{
		if(this.m_categoriesStack.size() > 1)
		{
			this.m_categoriesStack.pop();
			pushCategory(this.m_categoriesStack.pop());
		}
		else
		{
			this.m_currentViewOffset = 0;
			this.m_lastOffset = 0;
			nextIndiagram();
		}
	}
	
	public static int idCourant = 0;
	public synchronized void pushCategory(Category _category)
	{
		synchronized(this)
		{

			this.m_categoriesStack.push(_category);
			clearViewList();
			this.m_lastOffset = 0;
			this.m_currentViewOffset = 0;
			
			ArrayList<Indiagram> indiagrams = new ArrayList<Indiagram>();
			indiagrams.addAll(_category.indiagrams);
			
			int size = indiagrams.size();
			int textColor = _category.textColor;
			IndiagramView v = null;
			int id = idCourant;
			for(int i = 0 ; i < size ; ++i)
			{
				v = indiagrams.get(i).getView();
				v.setId(++id);
				v.setTextColor(textColor);
				this.m_currentViews.add(v);
				
				try
				{
					Mapper.connect(v, "touchEvent", this, "indiagramEvent");
				}
				catch(Exception ex)
				{
					Log.wtf("IndiagramBrowser", ex);
				}
			}
			idCourant = id;
		}
		nextIndiagram();
		
		try
		{
			Mapper.emit(this, "categoryChanged", _category);
		}
		catch(MapperException e)
		{
			Log.wtf("IndiagramBrowser", e);
		}
	}
	
	@SuppressWarnings("unused")
	public void nextIndiagram()
	{
		// TODO
		synchronized(this)
		{
			
			Log.e("IndiagramBrowser", "Je rafraichi �� chaque fois ?");

			// Je supprime toutes les vues du layout
			this.m_layout.removeAllViews();
			
			// Je place le boutton Next
			placeNextButton();

			// J'initialise les vues �� null
			for(int i = 0 ; i < m_displayableView.length ; ++i)
			{
				for(int j = 0 ; j < m_displayableView[i].length ; ++j)
				{
					m_displayableView[i][j] = null;
				}
			}

		
			this.m_lastOffset = this.m_currentViewOffset;
			int index = this.m_currentViewOffset;
			int id = idCourant;
			// Pour toute la grille de vues
			for(int i = 0 ; i < m_displayableView.length ; ++i)
			{
				for(int j = 0 ; j < m_displayableView[i].length && index < m_currentViews.size() ; ++j)
				{
					// Je r��cup��re l'indiagram
					
					IndiagramView v = m_currentViews.get(index++);
					v.setId(++id);
					//TODO Indiagram clone drag & drop
					// N'affiche pas les indiagram qui sont en bas ou en drag & drop
					while((v.m_isInDragAndDrop || this.m_sentence.hasIndiagram(v.getIndiagram())) && index < this.m_currentViews.size())
					{
						v = m_currentViews.get(index++);
					}
					
					if(v.m_isInDragAndDrop || this.m_sentence.hasIndiagram(v.getIndiagram()))
					{
						break;
					}
					
					// Je met la vue dans la grille
					m_displayableView[i][j] = v;
				}
			}
			idCourant = id;
			boolean stop = false;
			int maxOfPreviousLine = -1;
			int maxOfLine = -1;
			
			int currentHeight = 0;
			for(int i = 0 ; i < m_displayableView.length ; ++i)
			{
				maxOfPreviousLine = maxOfLine;
				maxOfLine = -1;
				for(int j = 0 ; j < m_displayableView[i].length ; ++j)
				{
					if(m_displayableView[i][j] == null)
					{
						stop = true;
						break;
					}
					
					// On cr��er des nouveaux param��tres
					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					
					// Si je suis sur la premi��re ligne je suis en haut
					if(i == 0)
					{
						param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					}
					else
					{
						// Sinon je suis en dessous de la ligne du dessus, de qu'elle J ?		
					//	Log.e("IndiagramBrowser"," Je suis india " + m_displayableView[i][j].getId() + " text " + m_displayableView[i][j].getIndiagram().text);
					//	Log.e("IndiagramBrowser"," Je vais ��tre en dessous de india " + m_displayableView[i-1][0].getId() + " text " + m_displayableView[i-1][0].getIndiagram().text);
						param.addRule(RelativeLayout.BELOW, m_displayableView[i-1][0].getId());
						
					}
					if(j == 0)
					{
						// Si je suis le premier de la ligne je suis �� gauche de mon parent
						param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					}
					else
					{
					//	Log.e("IndiagramBrowser"," Je suis india " + m_displayableView[i][j].getId() + " text " + m_displayableView[i][j].getIndiagram().text);
					//	Log.e("IndiagramBrowser"," Je vais ��tre �� droite de india " + m_displayableView[i][j-1].getId() + " text " + m_displayableView[i][j-1].getIndiagram().text);
						// Du coup je suis �� droite de celui qui est �� ma gauche
						param.addRule(RelativeLayout.RIGHT_OF, m_displayableView[i][j-1].getId());
						
					}

					// J'ajoute la vue
					this.m_layout.addView(m_displayableView[i][j], param);
					
					this.m_currentViewOffset = this.m_currentViews.indexOf(m_displayableView[i][j]) + 1;
					if(maxOfLine == -1 || m_displayableView[i][maxOfLine].getRealHeight() < m_displayableView[i][j].getRealHeight())
					{
						maxOfLine = j;
					}
				}
				
				if(maxOfLine >= 0)
				{
					currentHeight += m_displayableView[i][maxOfLine].getRealHeight();
					
					if(currentHeight > this.m_height)
					{
						stop = true;
						for(int j = 0 ; j < m_displayableView[i].length && m_displayableView[i][j] != null ; ++j)
						{
							this.m_layout.removeView(m_displayableView[i][j]);
							this.m_currentViewOffset--;
						}
					}
				}
				
				if(stop)
				{
					break;
				}
			}
		}
		Log.e("IndiagramBrowser", "Je sort dans next indiagram");

	}
	
	protected void placeNextButton(){
	

		if(this.m_nextVisibleState)
		{
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			
			IndiagramView view = AppData.nextButtonIndiagram.getView();
			this.m_layout.addView(view, lp);
		}
		
	}
	
	public void indiagramPositionChanged(IndiagramView _view)
	{
		synchronized(this)
		{
			if(this.m_categoriesStack.peek().hasChild(_view.getIndiagram()))
			{
				if(!_view.m_isInDragAndDrop && !this.m_sentence.hasIndiagram(_view.getIndiagram()))
				{
					_view.setId(getAvailableId());
					try
					{
						Mapper.connect(_view, "touchEvent", this, "indiagramEvent");
					}
					catch(Exception ex)
					{
						Log.wtf("IndigramBrowser", ex);
					}
				}
				this.m_currentViewOffset = this.m_lastOffset;
				nextIndiagram();
			}
		}
	}
	
	protected int getAvailableId()
	{
		int i = 1;
		for( ; this.m_layout.findViewById(i) != null ; ++i);
		return i;
	}
	
	public void disconnectIndiagram(IndiagramView _view)
	{
		Mapper.disconnect(_view, "touchEvent");
	}
	
	public void removeIndiagram(IndiagramView _view)
	{
		disconnectIndiagram(_view);
		_view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		this.m_layout.removeView(_view);
	}
	
	public boolean isMoreView()
	{
		if(this.m_currentViewOffset >= this.m_currentViews.size())
		{
			return false;
		}
		synchronized(this)
		{
			ArrayList<Indiagram> sentenceIndiagram = this.m_sentence.getIndiagramsList();
			for(int i = this.m_currentViewOffset ; i < this.m_currentViews.size() ; ++i)
			{
				if(!sentenceIndiagram.contains(this.m_currentViews.get(i).getIndiagram()) && !this.m_currentViews.get(i).m_isInDragAndDrop)
				{
					return true;
				}
			}
			return false;
		}
	}
	
	public synchronized void nextButtonEvent(IndiagramView _view, MotionEvent _event, EventResult _result)
	{
		try
		{
			if(_event.getActionMasked() == MotionEvent.ACTION_DOWN)
			{
				Mapper.emit(this, "nextButtonTouched", _view, _result);
			}
		}
		catch(MapperException e)
		{
			Log.wtf("IndiagramBrowser", e);
		}
	}
	
	public synchronized void indiagramEvent(IndiagramView _view, MotionEvent _event, EventResult _result)
	{		
		try
		{
			Mapper.emit(this, "indiagramEvent", _view, _event, _result);
		} 
		catch (MapperException e)
		{
			Log.wtf("IndiagramBrowser", e);
		}
	}
}
