package org.indiarose.lib;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;
import java.util.ArrayList;

import org.indiarose.lib.model.Indiagram;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * Class to manage all path relative system.
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class PathData
{
	/**
	 * The root path to all India Rose relative data.
	 */
	public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/IndiaRose/";
	
	/**
	 * Path to xml storage directory. 
	 */
	public static final String XML_DIRECTORY = ROOT_PATH + "xml/";
	/**
	 * Path to the image storage directory.
	 */
	public static final String IMAGE_DIRECTORY = ROOT_PATH + "image/";
	/**
	 * Name of the directory of the image took with camera.
	 */
	public static final String USER_IMAGE_DIRECTORY = ""; //TODO : "0x2Acamera/";
	/**
	 * Path to the sound storage directory.
	 */
	public static final String SOUND_DIRECTORY = ROOT_PATH + "sound/";
	/**
	 * Name of the directory of the sound recorded using microphone.
	 */
	public static final String USER_SOUND_DIRECTORY = ""; //TODO : "0x2Amicro/";
	/**
	 * Path to temp directory.
	 */
	public static final String TMP_DIRECTORY = ROOT_PATH + "temp/";
	/**
	 * Path to the internal app data directory.
	 */
	public static final String APP_DIRECTORY = ROOT_PATH + "app/";
	
	/**
	 * Path to the settings file.
	 */
	public static final String SETTINGS_FILE = APP_DIRECTORY + "settings.xml";
	/**
	 * Path to the application data file.
	 */
	public static final String APPDATA_FILE = APP_DIRECTORY + "appdata.xml";
	/**
	 * Path to the home category xml file.
	 */
	public static final String HOME_FILE = "../app/home.xml";
	/**
	 * Path to the changelog xml file.
	 */
	public static final String CHANGELOG_FILE = APP_DIRECTORY + "changelog.xml";
	/**
	 * Path to the next arrow xml file describing this indiagram. 
	 */
	public static final String NEXTARROW_FILE = "rightarrow.xml";
	/**
	 * Path to the play button xml file describing this indiagram. 
	 */
	public static final String PLAYBUTTON_FILE = "playbutton.xml";
	/**
	 * Initialize all path. If one does not exists, create all.
	 */
	public static void Initialize()
	{
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File(ROOT_PATH));
		files.add(new File(XML_DIRECTORY));
		files.add(new File(IMAGE_DIRECTORY));
		files.add(new File(IMAGE_DIRECTORY + USER_IMAGE_DIRECTORY));
		files.add(new File(SOUND_DIRECTORY));
		files.add(new File(SOUND_DIRECTORY + USER_SOUND_DIRECTORY));
		files.add(new File(TMP_DIRECTORY));
		files.add(new File(APP_DIRECTORY));
		
		for(int i = 0 ; i < files.size() ; ++i)
		{
			if(!files.get(i).exists())
			{
				if(!files.get(i).mkdir())
				{
					Log.e("PathData", "Unable to create " + files.get(i).getPath());
				}
			}
		}
	}
	
	/**
	 * Check if the app directory exists.
	 * @return true if exists.
	 */
	public static boolean IsAppDirectoryExists()
	{
		File rootPath = new File(ROOT_PATH);
		return rootPath.exists();
	}
	
	public static void clearTempDirectory()
	{
		File tmp = new File(TMP_DIRECTORY);
		if(tmp.exists())
		{
			for(String s : tmp.list())
			{
				File f = new File(s);
				deleteFile(f);
			}
		}
	}
	
	public static void deleteFile(File f)
	{
		if (f.isDirectory())
	        for (File child : f.listFiles())
	        	deleteFile(child);

	    f.delete();
	}
	
	
	/*
	public static String getDirectoryCategoryPath(Category _item)
	{
		return getDirectoryCategoryPath(_item, "");	
	}
	
	public static String getDirectoryCategoryPath(Category _item, String _prefix)
	{		
		String catName = getIndiagramPath(_item, _prefix);
		String catPath = catName.substring(0, catName.length() - ".xml".length()) + "/";
		
		File cat = new File(PathData.XML_DIRECTORY + catPath);
		if(!cat.exists())
		{
			cat.mkdirs();
		}
		
		return catPath;
	}
	*/
	
	/**
	 * Function to retrieve the path to an Indiagram file.
	 * @param _item : the indiagram to have path.
	 * @return the path to xml file.
	 * TODO integrate pack management with PACK_ instead of USER_
	 */	
	public static String getIndiagramPath(Indiagram _item)
	{
		if(TextUtils.isEmpty(_item.filePath))
		{
			String directory = PathData.XML_DIRECTORY;
			String filename = "USER_";
			for(int i = 0 ; i < _item.text.length() ; ++i)
			{
				char c = _item.text.charAt(i);
				if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
				{
					filename += c;
				}
			}
			
			File f = new File(directory + filename + ".xml");
			int index = 1;
			while(f.exists())
			{
				index++;
				f = new File(directory + filename + "_" + index + ".xml");
			}
			if(index > 1)
			{
				_item.filePath = filename + "_" + index + ".xml";
			}
			else
			{
				_item.filePath = filename + ".xml";
			}
		}
		return _item.filePath;
	}
	
	/*
	public static String getCompleteIndiagramPath(Indiagram _item)
	{
		return PathData.IMAGE_DIRECTORY + _item.imagePath;
	}
	*/
}
