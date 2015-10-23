package org.indiarose.lib;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.*;

import org.indiarose.library.R;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;
import org.indiarose.lib.model.xml.CategoryOrIndiagramXmlConverter;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;

public class Bootstrap {

	public interface BootstrapDelegate {
		public void onBootstrapInitialised();
	}

	protected static boolean initialized = false;
	private static ProgressDialog mProgressDialog = null;
	private static BootstrapDelegate delegate = null;

	public static void Initialize(final Context _startContext,
			BootstrapDelegate delegate) throws Exception {

		Bootstrap.delegate = delegate;

		if (Bootstrap.initialized) {
			delegate.onBootstrapInitialised();
			return;
		}

		Bootstrap.initialized = true;
		AppData.currentContext = _startContext;
		AppData.current_activity = (Activity) _startContext;

		creerProgressDialog();

		boolean copyFiles = false;
		if (!PathData.IsAppDirectoryExists()) {
			copyFiles = true;
		}

		/*
		 * do mkdirs, to set pathFiles architecture
		 */
		PathData.Initialize();

		try {
			boolean b = new File(PathData.ROOT_PATH + "" + ".nomedia")
			.createNewFile();
			if (!b && !new File(PathData.ROOT_PATH + "" + ".nomedia").exists()) {
				System.out.println("pb creer nomedia");
			}
		} catch (Exception e) {

		}

		boolean langueChanged = checkLanguageChanged();

		copierFichiers(copyFiles, langueChanged);

	}

	private static void copierFichiers(final boolean copyFiles,
			final boolean langueChanged) throws Exception {
		/*
		 * if langueChanged, we need to rm all the PathData.XML_DIRECTORY
		 */

		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					if (copyFiles)
						CopyFiles();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				AppData.Load(langueChanged);
				if (langueChanged) {
					langueChanged(copyFiles, langueChanged);
				} else {
					copierFichiers2(copyFiles, langueChanged);
				}
			}
		});

		th.start();
	}

	private static void copierFichiers2(final boolean copyFiles,
			final boolean langueChanged) {
		
		if (copyFiles || langueChanged) {
			afficherProgress();
			// attendreAffichageProgress();
		}
		
		try {
			if(copyFiles)
				CopyFiles();
			if (copyFiles || langueChanged){
				CollectionManager.ImportArchive(AppData.current_activity,
						mProgressDialog);
			}
			
			AppData.current_activity.runOnUiThread(new Runnable() {
				public void run() {
					fermerProgress();
					delegate.onBootstrapInitialised();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void afficherProgress() {

		AppData.current_activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.setProgress(0);
				mProgressDialog.setMessage(AppData.currentContext
						.getResources().getString(R.string.importArchive));
				mProgressDialog.show();
				mProgressDialog.onStart();
			}
		});

	}

	private static void langueChanged(final boolean copyFiles,
			final boolean langueChanged) {

		AppData.current_activity.runOnUiThread(new Runnable() {
			public void run(){

				AlertDialog.Builder builder = new AlertDialog.Builder(
						AppData.current_activity);
				builder.setMessage(R.string.warningChangeLangage);
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						
						dialog.cancel();
						
						// on vide le xmldirectory
						DeleteRecursive(new File(PathData.XML_DIRECTORY));

						new File(PathData.XML_DIRECTORY).mkdir();
						boolean b = new File(PathData.XML_DIRECTORY).exists();

						try {
							copierFichiers2(true, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							
							dialog.cancel();
							
							copierFichiers2(copyFiles, !langueChanged);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				builder.setCancelable(false);
				final AlertDialog dialog = builder.create();

				dialog.show();

			}
		});
	}

	private static void fermerProgress() {
		mProgressDialog.dismiss();
	}

	private static void attendreAffichageProgress() {
		while (!mProgressDialog.isShowing()) {
			System.out.println(" ");
		}
	}

	private static void creerProgressDialog() {
		mProgressDialog = new ProgressDialog(AppData.currentContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle(AppData.currentContext
				.getString(R.string.loading));
		mProgressDialog.setMessage(AppData.currentContext
				.getString(R.string.startLoading));

		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(0);
		mProgressDialog.setCancelable(false);
	}

	static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}

	private static void saveLangue(String filepath, String data) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(filepath))));
			writer.write(data);
		} catch (Exception e) {
			Log.d("savelangue1", e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					Log.d("savelangue", e.getMessage());
				}
			}
		}
	}

	private static boolean checkLanguageChanged() {
		boolean langueChanged = false;
		// Log.d("langue",
		// _activ.getResources().getConfiguration().locale.getDisplayLanguage());
		if (!new File(PathData.APP_DIRECTORY + "/langue.txt").exists()) {
			// pas de fichier
			// Log.d("langue0", "langue.txt does not exists");
			boolean b = false;
			try {
				b = new File(PathData.APP_DIRECTORY + "/langue.txt")
				.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!b) {
				// Log.d("langue1", "fail create langue.txt");
			} else {
				saveLangue(PathData.APP_DIRECTORY + "/langue.txt",
						AppData.currentContext.getResources()
						.getConfiguration().locale.getDisplayLanguage());
			}

		} else {
			// fichiers deja la, on va voir comment ca se passe

			// Log.d("langue2", "langue.txt exist");
			String lg = getLangueSave(PathData.APP_DIRECTORY + "/langue.txt");
			// Log.d("langue2.5", "lg = "+lg);
			if (!lg.equals(AppData.currentContext.getResources()
					.getConfiguration().locale.getDisplayLanguage())) {
				// Log.d("langue3", "langue changed");
				saveLangue(PathData.APP_DIRECTORY + "/langue.txt",
						AppData.currentContext.getResources()
						.getConfiguration().locale.getDisplayLanguage());
				langueChanged = true;
			} else {
				// Log.d("langue4", "langue doesn't changed");
				langueChanged = false;
			}

		}

		return langueChanged;
	}

	private static String getLangueSave(String path) {
		String monText = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path))));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line);
			}
			monText = buffer.toString();
		} catch (Exception e) {
			Log.d("getlangue1", e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e2) {
					Log.d("getlangue2", e2.getMessage());
				}
			}
		}
		return monText;
	}

	public static void Uninitialize(boolean _save) throws Exception {
		Log.e("bootstrap uninitialize", "ca passe");
		AppData.SaveSettings();
		if (_save) {
			AppData.SaveHomeCategory();
		}
	}

	protected static void CopyFiles() throws Exception {
		try {
			// create base file for rightarrow and playbutton
			copyRessource(R.raw.playbutton, PathData.APP_DIRECTORY
					+ "playbutton.png");
			copyRessource(R.raw.rightarrow, PathData.APP_DIRECTORY
					+ "nextarrow.png");
			copyRessource(R.raw.root, PathData.APP_DIRECTORY + "root.png");
			copyRessource(R.raw.correction, PathData.APP_DIRECTORY
					+ "correction.png");

			Indiagram play = new Indiagram("", "../app/playbutton.png", "");
			Indiagram next = new Indiagram("", "../app/nextarrow.png", "");
			String s = AppData.current_activity.getResources().getString(
					R.string.homeCategoryName);
			// Log.d("Bootstrap - CopyFiles", s);
			boolean b = new File(PathData.APP_DIRECTORY + PathData.HOME_FILE)
			.delete();
			if (!b) {
				// Log.d("Bootstrap - copyFiles", "fail delete");
			}
			b = new File(PathData.APP_DIRECTORY + PathData.HOME_FILE).exists();
			if (!b) {
				// Log.d("Bootstrap - copyFiles", "home deleted");
			} else {
				// Log.d("Bootstrap - copyFiles", "home pas deleted");
			}
			Category home = new Category(s, "../app/root.png", "", Color.LTGRAY);
			AppData.homeCategory = home;
			// Log.d("Bootstrap - copyFiles", "home.text : "+
			// AppData.homeCategory.text);
			CategoryOrIndiagramXmlConverter writer = CategoryOrIndiagramXmlConverter
					.getInstance();
			writer.write(play, PathData.PLAYBUTTON_FILE, PathData.APP_DIRECTORY);
			writer.write(next, PathData.NEXTARROW_FILE, PathData.APP_DIRECTORY);
			writer.write(home, PathData.HOME_FILE, PathData.APP_DIRECTORY);
		} catch (Exception ex) {
			Log.wtf("Bootstrap I", ex);
		}
	}

	protected static void copyRessource(int _resId, String _destination)
			throws Exception {
		BufferedInputStream input = new BufferedInputStream(
				AppData.currentContext.getResources().openRawResource(_resId));
		byte[] buffer = new byte[1024];
		FileOutputStream output = new FileOutputStream(_destination);
		int length = 0;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}

		output.close();
		input.close();
	}
}
