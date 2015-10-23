package org.indiarose.lib.model;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.util.ArrayList;

import android.graphics.Color;

/**
 * Class to manage category entity.
 * @see org.indiarose.lib.model.Indiagram
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class Category extends Indiagram 
{
	/**
	 * List of all indiagram children.
	 */
	public ArrayList<Indiagram> indiagrams = new ArrayList<Indiagram>();
	
	/**
	 * The color of the text in the category.
	 */
	public int textColor = Color.LTGRAY;
	
	/**
	 * Construct a default Category
	 */
	public Category()
	{
		
	}
	
	/**
	 * Construct a Category and fill its field.
	 * @param _text : the text of the category.
	 * @param _imagePath : the image path of the category.
	 * @param _soundPath : the sound path of the category.
	 * @param _textColor : the color of the text to use into the category.
	 */
	public Category(String _text, String _imagePath, String _soundPath, int _textColor) 
	{
		super(_text, _imagePath, _soundPath);
		this.textColor = _textColor;
	}
	
	@Override
	public boolean equals(Object _other)
	{
		if(!(_other instanceof Category))
		{
			return false;
		}
		
		Category other = (Category)_other;
		
		return  super.equals(_other) &&
				(other.textColor == this.textColor) &&
				other.indiagrams.containsAll(this.indiagrams) && 
				this.indiagrams.containsAll(other.indiagrams);
	}
	
	@Override
	public Category clone()
	{
		Category other = new Category(this.text, this.imagePath, this.soundPath, this.textColor);
		
		other.filePath = this.filePath;
		other.indiagrams.addAll(this.indiagrams);
		
		return other;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.indiarose.lib.model.Indiagram#resetFileData()
	 */
	@Override
	public void resetFileData()
	{
		super.resetFileData();
		for(int i = 0 ; i < indiagrams.size() ; ++i)
		{
			indiagrams.get(i).resetFileData();
		}
	}
	
	/**
	 * Method to check whether this category has a specified child or not.
	 * @param _item : the child you want to find.
	 * @return true if it is a child of this category, false otherwise.
	 */
	public boolean hasChild(Indiagram _item)
	{
		if(this.indiagrams.contains(_item))
		{
			return true;
		}
		return false;
	}
}
