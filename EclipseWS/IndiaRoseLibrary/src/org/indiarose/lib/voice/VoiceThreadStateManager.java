package org.indiarose.lib.voice;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */


public class VoiceThreadStateManager extends Thread
{
	protected VoiceEngine m_engine = null;
	
	public VoiceThreadStateManager(VoiceEngine _engine)
	{
		this.m_engine = _engine;
	}
	
	public void run()
	{
		while(true)
		{
			while(!this.m_engine.isReading())
			{
				try
				{
					this.wait(70);
				}
				catch(Exception e)
				{
					
				}
			}
			while(this.m_engine.isReading())
			{
				try
				{
					this.wait(70);
				}
				catch(Exception e)
				{
					
				}
			}
			this.m_engine.endReading();
		}
	}
}
