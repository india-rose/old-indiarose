package org.indiarose.lib;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.model.xml.CategoryOrIndiagramXmlConverter;
import org.indiarose.lib.utils.XmlFilenameFilter;

import android.app.Activity;
import android.app.ProgressDialog;

import org.indiarose.R;

/**
 * This class manage indigrams collections.
 * 
 * @author Julien Mialon <mialon.julien@gmail.com>
 */
public class CollectionManager
{
	private static ProgressDialog mProgressDialog;
	public static void Delete(Indiagram _indiagram, Category _parent)
	{
		_parent.indiagrams.remove(_indiagram);
		File f = new File(PathData.XML_DIRECTORY + _indiagram.filePath);
		f.delete();
		if(_indiagram instanceof Category)
		{
			File d = new File(PathData.XML_DIRECTORY + _indiagram.filePath.substring(0, _indiagram.filePath.length() - ".xml".length()));
			d.delete();
		}
		_indiagram.filePath = "";
	}

	public static void ChangeCategory(Indiagram _indiagram, Category _original, Category _new)
	{
		Delete(_indiagram, _original);
		_new.indiagrams.add(_indiagram);
	}

	public static void ImportArchive(final Activity _activ, ProgressDialog _mProgressDialog) throws Exception
	{
		mProgressDialog=_mProgressDialog;
		mProgressDialog.setMax(3);
		try
		{	
			_activ.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mProgressDialog.setProgress(1);
					mProgressDialog.setMessage(_activ.getResources().getString(R.string.decompressionArchive));
				}
			});
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(AppData.currentContext.getResources().openRawResource(R.raw.indiagrams)));
			try
			{
				ZipEntry entry = null;
				int count = 0;
				byte[] buffer = new byte[1024];
				PathData.clearTempDirectory();

				while((entry = zip.getNextEntry()) != null)
				{
					try
					{
						if(!entry.isDirectory())
						{

							String filename = entry.getName();
							String path = "";
							if(filename.endsWith(".xml"))
							{
								path = PathData.TMP_DIRECTORY;
							}
							else if(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".gif"))
							{
								path = PathData.IMAGE_DIRECTORY;
							}

							path += filename;

							File f = new File(path);
							if(!f.getParentFile().exists())
							{
								f.getParentFile().mkdirs();
							}

							FileOutputStream fout = new FileOutputStream(path);
							try
							{
								while ((count = zip.read(buffer)) != -1) 
								{
									fout.write(buffer, 0, count);             
								}
							}
							finally
							{
								fout.close();
							}
						}
						else
						{
							
						}
					}
					finally
					{
						zip.closeEntry(); 
					}
				}
			}
			finally
			{
				zip.close();
			}
		}
		catch(Exception ex)
		{

		}
		_activ.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.setProgress(2);
				mProgressDialog.setMessage(_activ.getResources().getString(R.string.ajout_des_indiagrams));
			}
		});


//		solution avec ressources dans string.xml
		/*
		 * Loading indiagramsn with dynamic path, searching them in the zip file, 
		 * sub-directory defined in string.xml, for each langage, 
		 * then classical loading : each image is unzipped in IMAGE_DIRECTORY, and
		 * xml files are put in xml directory, sorted by category
		 */
		File xmlDirectory = new File(PathData.TMP_DIRECTORY+_activ.getResources().getString(R.string.pathXmls));
		
		ArrayList<Indiagram> indiagrams = new ArrayList<Indiagram>();
		for(File f : xmlDirectory.listFiles(new XmlFilenameFilter()))
		{

			indiagrams.add(CategoryOrIndiagramXmlConverter.getInstance().read(f.getName(), PathData.TMP_DIRECTORY+_activ.getResources().getString(R.string.pathXmls)));
			
		}
		for(int i = 0 ; i < indiagrams.size() ; ++i)
		{
			AppData.homeCategory.indiagrams.add(indiagrams.get(i));	 
		}


		AppData.SaveHomeCategory();

		_activ.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.setProgress(3);
				mProgressDialog.setMessage(_activ.getResources().getString(R.string.finalisation));
			}
		});



		AppData.cleanPath();
		PathData.clearTempDirectory();
	}
}
