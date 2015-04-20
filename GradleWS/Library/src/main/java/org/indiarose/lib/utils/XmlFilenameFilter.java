package org.indiarose.lib.utils;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.io.FilenameFilter;

public class XmlFilenameFilter implements FilenameFilter
{

	public boolean accept(File dir, String filename)
	{
		return filename.endsWith(".xml");
	}

}
