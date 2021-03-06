package org.indiarose.lib;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.File;

import org.indiarose.lib.model.*;
import org.indiarose.lib.model.xml.*;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * This class manage all visible data from the application representation.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class AppData {
	/**
	 * List of all indiagram in the root category.
	 */
	// public static ArrayList<Indiagram> homeIndiagrams = new
	// ArrayList<Indiagram>();

	public static Category homeCategory = new Category();

	/**
	 * The Settings object.
	 */
	public static Settings settings = null;

	/**
	 * The InternalAppValue object.
	 */
	public static InternalAppValue appData = null;

	/**
	 * The ChangeLog object.
	 */
	public static ChangeLog changeLog = null;

	/**
	 * The current app context.
	 */
	public static Context currentContext = null;

	/**
	 * The play button indiagram information.
	 */
	public static Indiagram playButtonIndiagram = null;

	/**
	 * The next arrow button indiagram information.
	 */
	public static Indiagram nextButtonIndiagram = null;

	/**
	 * 
	 */
	public static Indiagram current_indiagram = null;

	public static Activity current_activity = null;

	/**
	 * This method load all data into this class.
	 * 
	 * @throws Exception
	 */
	public static void Load() {
		SettingsXmlConverter settingsLoader = SettingsXmlConverter
				.getInstance();
		InternalAppValueXmlConverter internalAppLoader = InternalAppValueXmlConverter
				.getInstance();
		ChangeLogXmlConverter changeLogLoader = ChangeLogXmlConverter
				.getInstance();

		try {
			AppData.settings = settingsLoader.read(PathData.SETTINGS_FILE);
		} catch (Exception e) {
			Log.wtf("AppData", e);
		} finally {
			if (AppData.settings == null) {
				Log.wtf("AppData", "CREATION DES SETTINGS");
				AppData.settings = new Settings();
			}
		}

		try {
			AppData.appData = internalAppLoader.read(PathData.APPDATA_FILE);
		} catch (Exception e) {
			// Log.wtf("AppData", e);
		} finally {
			if (AppData.appData == null) {
				AppData.appData = new InternalAppValue();
			}
		}

		try {
			AppData.changeLog = changeLogLoader.read(PathData.CHANGELOG_FILE);
		} catch (Exception e) {
			// Log.wtf("AppData", e);
		} finally {
			if (AppData.changeLog == null) {
				AppData.changeLog = new ChangeLog();
			}
		}

		try {
			ReloadIndiagram();
		} catch (Exception ex) {
			Log.wtf("AppData", ex);
		}

		/*
		 * AppData.appData.login = "teamandroid"; RequestExecuter executer = new
		 * RequestExecuter(); executer.start();
		 * executer.setRequest(CloudRequest.deleteIndiagram("USER_cole"));
		 * 
		 * 
		 * RequestExecuter executer2 = new RequestExecuter(); executer2.start();
		 * executer2.setRequest(CloudRequest.deleteIndiagram("USER_test"));
		 */
	}

	/**
	 * Method to refresh the list of all root indiagrams or category.
	 * 
	 * @throws Exception
	 */
	public static void ReloadIndiagram() throws Exception {
		CategoryOrIndiagramXmlConverter reader = CategoryOrIndiagramXmlConverter
				.getInstance();
		// File xmlDirectory = new File(PathData.XML_DIRECTORY);

		/*
		 * if(xmlDirectory.exists()) { String [] files = xmlDirectory.list(new
		 * XmlFilenameFilter()); Arrays.sort(files); for(String filename :
		 * files) { homeIndiagrams.add(reader.read(filename,
		 * PathData.XML_DIRECTORY)); } }
		 */
		AppData.homeCategory = (Category) reader.read(PathData.HOME_FILE,
				PathData.XML_DIRECTORY);

		if (AppData.homeCategory == null) {
			Log.d("AppData", "ERROR home");
			// AppData.homeCategory = new Category();
		}

		AppData.playButtonIndiagram = reader.read(PathData.PLAYBUTTON_FILE,
				PathData.APP_DIRECTORY);
		AppData.nextButtonIndiagram = reader.read(PathData.NEXTARROW_FILE,
				PathData.APP_DIRECTORY);
	}

	/**
	 * This method save all application data.
	 * 
	 * @throws Exception
	 */
	public static void SaveSettings() throws Exception {
		SettingsXmlConverter settingsLoader = SettingsXmlConverter
				.getInstance();
		InternalAppValueXmlConverter internalAppLoader = InternalAppValueXmlConverter
				.getInstance();
		ChangeLogXmlConverter changeLogLoader = ChangeLogXmlConverter
				.getInstance();

		settingsLoader.write(AppData.settings, PathData.SETTINGS_FILE);
		internalAppLoader.write(AppData.appData, PathData.APPDATA_FILE);
		changeLogLoader.write(AppData.changeLog, PathData.CHANGELOG_FILE);
	}

	/*
	 * public synchronized static Category getHomeCategory() {
	 * 
	 * if(homeCategory == null) { homeCategory = new
	 * Category(AppData.currentContext.getString(R.string.homeCategoryName),
	 * "../app/root.png", "", Color.LTGRAY); homeCategory.filePath = "root.xml";
	 * 
	 * for(int i = 0 ; i < homeIndiagrams.size() ; ++i) {
	 * homeCategory.indiagrams.add(homeIndiagrams.get(i)); } }
	 * 
	 * return homeCategory; }
	 */

	public synchronized static void settingsChanged() {
		AppData.changeLog.settingsChanged();
	}

	public static void SaveHomeCategory() {
		try {
			CategoryOrIndiagramXmlConverter.getInstance().write(homeCategory,
					PathData.HOME_FILE, PathData.XML_DIRECTORY);
		} catch (Exception e) {
			Log.d("AppData", "Unable to save home category.", e);
		}

		for (int i = 0; i < homeCategory.indiagrams.size(); ++i) {
			Save(homeCategory.indiagrams.get(i));
		}
	}

	@SuppressWarnings("unused")
	public static void Save(Indiagram _indiagram) {
		try {
			// Log.d("test dirPath", PathData.getIndiagramPath(_indiagram));
			if (PathData.getIndiagramPath(_indiagram).contains("/")) {
				/*
				 * sub categories
				 */
				boolean b = new File(PathData.XML_DIRECTORY
						+ "/"
						+ PathData.getIndiagramPath(_indiagram).substring(
								0,
								PathData.getIndiagramPath(_indiagram)
										.lastIndexOf("/")) + "/").mkdirs();
				// if(b && new File(PathData.XML_DIRECTORY +"/"+
				// PathData.getIndiagramPath(_indiagram).substring(0,
				// PathData.getIndiagramPath(_indiagram).lastIndexOf("/"))+"/").exists()){
				// Log.d("erreur mkdirs",
				// PathData.getIndiagramPath(_indiagram).substring(0,
				// PathData.getIndiagramPath(_indiagram).lastIndexOf("/")) +
				// " created");
				// }
			}

			CategoryOrIndiagramXmlConverter.getInstance().write(_indiagram,
					PathData.getIndiagramPath(_indiagram),
					PathData.XML_DIRECTORY);

		} catch (Exception e) {
			Log.wtf("AppData", "Unable to save indiagram "
					+ _indiagram.filePath);
		}
		if (_indiagram instanceof Category) {
			Category c = (Category) _indiagram;
			for (int i = 0; i < c.indiagrams.size(); ++i) {
				Save(c.indiagrams.get(i));
			}
		}
	}

	public static void cleanPath() {
		for (int i = 0; i < AppData.homeCategory.indiagrams.size(); ++i) {
			cleanPath(AppData.homeCategory.indiagrams.get(i));
		}
	}

	protected static void cleanPath(Indiagram _indiagram) {
		_indiagram.filePath = "";
		PathData.getIndiagramPath(_indiagram);
		if (_indiagram instanceof Category) {
			Category c = (Category) _indiagram;
			for (int i = 0; i < c.indiagrams.size(); ++i) {
				cleanPath(c.indiagrams.get(i));
			}
		}
	}

	public static void Load(boolean langueChanged) {
		// TODO Auto-generated method stub
		SettingsXmlConverter settingsLoader = SettingsXmlConverter
				.getInstance();
		InternalAppValueXmlConverter internalAppLoader = InternalAppValueXmlConverter
				.getInstance();
		ChangeLogXmlConverter changeLogLoader = ChangeLogXmlConverter
				.getInstance();

		try {
			AppData.settings = settingsLoader.read(PathData.SETTINGS_FILE);
		} catch (Exception e) {
			Log.wtf("AppData", e);
		} finally {
			if (AppData.settings == null) {
				AppData.settings = new Settings();
			}
		}

		try {
			AppData.appData = internalAppLoader.read(PathData.APPDATA_FILE);
		} catch (Exception e) {
			Log.wtf("AppData", e);
		} finally {
			if (AppData.appData == null) {
				AppData.appData = new InternalAppValue();
			}
		}

		try {
			AppData.changeLog = changeLogLoader.read(PathData.CHANGELOG_FILE);
		} catch (Exception e) {
			// Log.wtf("AppData", e);
		} finally {
			if (AppData.changeLog == null) {
				AppData.changeLog = new ChangeLog();
			}
		}

		try {
			if (!langueChanged) {
				ReloadIndiagram();
			}
		} catch (Exception ex) {
			Log.wtf("AppData", ex);
		}

		/*
		 * AppData.appData.login = "teamandroid"; RequestExecuter executer = new
		 * RequestExecuter(); executer.start();
		 * executer.setRequest(CloudRequest.deleteIndiagram("USER_cole"));
		 * 
		 * 
		 * RequestExecuter executer2 = new RequestExecuter(); executer2.start();
		 * executer2.setRequest(CloudRequest.deleteIndiagram("USER_test"));
		 */
		CategoryOrIndiagramXmlConverter reader = CategoryOrIndiagramXmlConverter
				.getInstance();
		try {
			AppData.playButtonIndiagram = reader.read(PathData.PLAYBUTTON_FILE,
					PathData.APP_DIRECTORY);
			AppData.nextButtonIndiagram = reader.read(PathData.NEXTARROW_FILE,
					PathData.APP_DIRECTORY);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
