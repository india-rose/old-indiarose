package org.indiarose.lib.model;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;

/**
 * Class to manage local changes between cloud synchronization
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class ChangeLog
{
	public boolean settingsChanged = false;
	
	public ArrayList<String> indiagramAdded = new ArrayList<String>();
	public ArrayList<String> indiagramDeleted = new ArrayList<String>();
	public ArrayList<String> indiagramChanged = new ArrayList<String>();
	
	public ChangeLog()
	{
		
	}
	
	public void settingsChanged()
	{
		settingsChanged = true;
	}
	
	public void addIndiagram(Indiagram _indiagram)
	{
		addIndiagram(extractName(_indiagram));
	}
	
	public void addIndiagram(String _indiagramName)
	{
		indiagramAdded.add(_indiagramName);
	}
	
	public void changeIndiagram(Indiagram _indiagram)
	{
		changeIndiagram(extractName(_indiagram));
	}
	
	public void changeIndiagram(String _indiagramName)
	{
		indiagramChanged.add(_indiagramName);
	}
	
	public void deleteIndiagram(Indiagram _indiagram)
	{
		deleteIndiagram(extractName(_indiagram));
	}
	
	public void deleteIndiagram(String _indiagramName)
	{
		indiagramDeleted.add(_indiagramName);
	}
	
	protected String extractName(Indiagram _indiagram)
	{
		return _indiagram.filePath.substring(0, _indiagram.filePath.length() - ".xml".length());
	}
}
