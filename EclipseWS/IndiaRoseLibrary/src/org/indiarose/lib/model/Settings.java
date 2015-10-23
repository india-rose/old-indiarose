package org.indiarose.lib.model;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import android.graphics.Color;


/**
 * Class to manage global settings of the application
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class Settings 
{
	/**
	 * The height of the selection area in percentage.
	 */
	public int heightSelectionArea = 70;
	
	/**
	 * The background color of the selection area.
	 */
	public int backgroundSelectionArea = Color.parseColor("#2E8AEF");
	
	/**
	 * The background color of the phrase area.
	 */
	public int backgroundSentenceArea = Color.parseColor("#FFFFFF");
	
	/**
	 * The background around indiagram while reading them.
	 */
	public int backgroundReinforcerReading = Color.parseColor("#880088");
	
	/**
	 * Iso code of the language of the user.
	 */
	public String languageIsoCode = "fr";
	
	/**
	 * The width and height to display indiagrams.
	 */
	public int indiagramSize = 128;
	
	/**
	 * The font family to use to display indiagram's text
	 */
	public String fontFamily = "/system/fonts/Clockopia.ttf";
	
	/**
	 * The font size to use.
	 */
	public int fontSize = 20;
	
	/**
	 * Delay between two words reading.
	 */
	public float wordsReadingDelay = 1;
	
	/**
	 * Enable the drag and drop or not.
	 * If not, the point and click mode should be use.
	 */
	public Boolean enableDragAndDrop = false;
	
	/**
	 * Enable the reading of the category name when entering into it.
	 */
	public Boolean enableCategoryReading = true;
	
	/**
	 * Enable the reinforcer while reading indiagram.
	 * This reinforcer consist of a change of the background around the indiagram.
	 */
	public Boolean enableReadingReinforcer = true;
	
	/**
	 * Enable the reinforcer while selecting indiagram.
	 * This reinforcer consist of a reading of the sound associated with the indiagram
	 * or if no sound provided, a vocal synthesis of the text of the indiagram.
	 */
	public Boolean enableSelectionReinforcer = true;
	
	@Override
	public boolean equals(Object _other)
	{
		if(!(_other instanceof Settings))
		{
			return false;
		}
		Settings other = (Settings)_other;
		
		return  (other.backgroundSentenceArea == this.backgroundSentenceArea) &&
				(other.backgroundReinforcerReading == this.backgroundReinforcerReading) &&
				(other.backgroundSelectionArea == this.backgroundSelectionArea) && 
				(other.enableCategoryReading == this.enableCategoryReading) &&
				(other.enableDragAndDrop == this.enableDragAndDrop) &&
				(other.enableReadingReinforcer == this.enableReadingReinforcer) &&
				(other.enableSelectionReinforcer == this.enableSelectionReinforcer) &&
				(other.fontFamily.equalsIgnoreCase(this.fontFamily)) &&
				(other.fontSize == this.fontSize) &&
				(other.heightSelectionArea == this.heightSelectionArea) &&
				(other.indiagramSize == this.indiagramSize) &&
				(other.languageIsoCode.equalsIgnoreCase(this.languageIsoCode)) &&
				(other.wordsReadingDelay == this.wordsReadingDelay);
	}
	
	@Override
	public Settings clone()
	{
		Settings other = new Settings();
		
		other.backgroundSentenceArea = this.backgroundSentenceArea;
		other.backgroundReinforcerReading = this.backgroundReinforcerReading;
		other.backgroundSelectionArea = this.backgroundSelectionArea;
		other.enableCategoryReading = this.enableCategoryReading;
		other.enableDragAndDrop = this.enableDragAndDrop;
		other.enableReadingReinforcer = this.enableReadingReinforcer;
		other.enableSelectionReinforcer = this.enableSelectionReinforcer;
		other.fontFamily = this.fontFamily;
		other.fontSize = this.fontSize;
		other.heightSelectionArea = this.heightSelectionArea;
		other.indiagramSize = this.indiagramSize;
		other.languageIsoCode = this.languageIsoCode;
		other.wordsReadingDelay = this.wordsReadingDelay;
		
		return other;
	}
}
