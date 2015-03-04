package org.indiarose.backend.activity;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.*;

import org.indiarose.R;
import org.indiarose.api.IndiagramManager;
import org.indiarose.backend.ScreenManager;
import org.indiarose.backend.view.element.SynchroProgressBar;
import org.indiarose.frontend.PictoSelection;
import org.indiarose.lib.*;
import org.indiarose.lib.Bootstrap.BootstrapDelegate;
import org.indiarose.lib.cloud.Synchro;

import storm.communication.Mapper;
import storm.communication.MapperException;
import android.app.*;
import android.content.*;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class Home extends Activity implements View.OnClickListener,
BootstrapDelegate {
	View credits;
	View help;
	View contact;
	View appSettings;
	View collectionManagement;
	View synchronize;
	View quit;
	View sendlog;
	View installTTS;

	IndiagramManager indiagramManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (AppData.current_activity != null
				&& AppData.current_activity.getClass() == PictoSelection.class)
			AppData.current_activity.finish();

		AppData.current_activity = this;

		load();

		// System.out.println(indiagramManager.getRootCategories().toString());

	}

	protected void ajouterVues() {
		credits = findViewById(R.id.credits);
		contact = findViewById(R.id.contact);
		help = findViewById(R.id.aide);
		appSettings = findViewById(R.id.home_app_settings);
		collectionManagement = findViewById(R.id.home_collection_management);
		synchronize = findViewById(R.id.home_synchronize);
		quit = findViewById(R.id.home_quit);
		sendlog = findViewById(R.id.home_sendlog);
		installTTS = findViewById(R.id.home_install_tts);
	}

	protected void ajouterListeners() {
		credits.setOnClickListener(this);
		contact.setOnClickListener(this);
		help.setOnClickListener(this);
		appSettings.setOnClickListener(this);
		collectionManagement.setOnClickListener(this);
		synchronize.setOnClickListener(this);
		quit.setOnClickListener(this);
		sendlog.setOnClickListener(this);
		installTTS.setOnClickListener(this);
	}

	protected void launchAppSettings() {
		Intent i = new Intent(this, AppSettings.class);
		startActivity(i);
		finish();
	}

	protected void launchCollection() {
		Intent i = new Intent(this, CollectionManagement.class);
		startActivity(i);
		finish();
	}
	
	protected void lancerContact() {
		Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_title));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources().getString(R.string.email_address) });
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));
        startActivity(Intent.createChooser(intent, ""));
	}

	protected void synchronize() {
		if (checkInternetConnection()) {
			LinearLayout dialogLayout = new LinearLayout(this);
			LinearLayout loginLayout = new LinearLayout(this);
			LinearLayout passLayout = new LinearLayout(this);

			TextView loginLabel = new TextView(this);
			TextView passLabel = new TextView(this);
			final EditText loginEdit = new EditText(this);
			final EditText passEdit = new EditText(this);

			loginEdit.setMinimumWidth(200);
			passEdit.setMinimumWidth(200);

			loginLabel.setTextSize(ScreenManager.fontSize);
			passLabel.setTextSize(ScreenManager.fontSize);
			loginEdit.setTextSize(ScreenManager.fontSize);
			passEdit.setTextSize(ScreenManager.fontSize);
			loginLabel.setTextColor(Color.LTGRAY);
			passLabel.setTextColor(Color.LTGRAY);
			loginEdit.setTextColor(Color.BLACK);
			passEdit.setTextColor(Color.BLACK);
			loginLabel.setText(R.string.loginLabel);
			passLabel.setText(R.string.passLabel);
			loginEdit.setText(AppData.appData.login);
			passEdit.setText(AppData.appData.password);
			passEdit.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);

			loginLayout.setOrientation(LinearLayout.HORIZONTAL);
			loginLayout.addView(loginLabel);
			loginLayout.addView(loginEdit);
			passLayout.setOrientation(LinearLayout.HORIZONTAL);
			passLayout.addView(passLabel);
			passLayout.addView(passEdit);

			dialogLayout.setOrientation(LinearLayout.VERTICAL);
			dialogLayout.addView(loginLayout);
			;
			dialogLayout.addView(passLayout);

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setView(dialogLayout);
			adb.setCancelable(false);
			adb.setTitle(R.string.synchroCredentials);
			adb.setIcon(android.R.drawable.ic_dialog_alert);
			adb.setPositiveButton(R.string.okText,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface _dialog, int _which) {
					AppData.appData.login = loginEdit.getText()
							.toString();
					AppData.appData.password = passEdit.getText()
							.toString();
					_dialog.dismiss();
					initSynchro();
				}
			});
			adb.setNegativeButton(R.string.cancelText,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface _dialog, int _which) {
					_dialog.dismiss();
				}
			});
			adb.show();
		} else {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.noInternet);
			adb.setIcon(android.R.drawable.ic_dialog_alert);
			adb.setNegativeButton(R.string.cancelText,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface _dialog, int _which) {
					_dialog.dismiss();
				}
			});
			adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface _dialog) {
					_dialog.dismiss();
				}
			});
			adb.show();
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.home_app_settings:
			launchAppSettings();
			break;

		case R.id.home_collection_management:
			launchCollection();
			break;

		case R.id.home_synchronize:
			synchronize();
			break;

		case R.id.credits:
			launchCredits();
			break;

		case R.id.aide:
			launchAide();
			break;

		case R.id.home_quit:
			save();
			onDestroy();
			break;
		case R.id.home_sendlog:
			sendLogs();
			break;

		case R.id.home_install_tts:
			installTTS();
			break;
			
		case R.id.contact:
			lancerContact();
			break;
			
		}
	}

	private void installTTS() {
		Intent intent = new Intent(this, InstallTTS.class);
		startActivity(intent);
		finish();
	}

	private void sendLogs() {
		new IndiaLogger().sendLogs(this);
		// Toast.makeText(this, getResources().getString(R.string.sendLogToast),
		// Toast.LENGTH_SHORT).show();

	}

	public void launchCredits() {
		Intent intent = new Intent(this, Credits.class);
		startActivity(intent);
		finish();
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public void launchAide() {
		AssetManager assetManager = getAssets();

		InputStream in = null;
		OutputStream out = null;
		File file = new File(getFilesDir(), "manuel.pdf");
		try {
			in = assetManager.open("manuel.pdf");
			out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.parse("file://" + getFilesDir() + "/manuel.pdf"),
					"application/pdf");

			startActivity(intent);
		}catch(Exception e){
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pdf&c=apps")));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pdf&c=apps")));
			}
		}
	}

	/**
	 * Method to check if an internet connection is available.
	 * 
	 * @return true if connection detected, false otherwise.
	 */
	protected boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable() && cm
				.getActiveNetworkInfo().isConnected());
	}

	protected void initSynchro() {
		// build adb + create synchro + synchroProgressBar + connect them

		Synchro sync = new Synchro();
		SynchroProgressBar bar = new SynchroProgressBar();

		try {
			Mapper.connect(sync, "progress", bar, "progress");
			Mapper.connect(sync, "finished", this, "endSynchro");
		} catch (MapperException e) {
			Log.wtf("Home", "unable to connect synchro", e);
		}

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setView(bar.getView());
		adb.setCancelable(false);
		adb.setTitle(R.string.synchroLabel);
		adb.setIcon(android.R.drawable.ic_dialog_alert);

		adb.show();

		sync.execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		save();
		onDestroy();

	}

	/**
	 * Kill the process when finished
	 */
	protected void save() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage(getResources().getString(R.string.save_in_progress));
		progress.setCancelable(false);
		progress.show();

		while (!progress.isShowing()) {
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					Bootstrap.Uninitialize(true);
					Log.e("home save", "fin save");
					finish();
				} catch (Exception e) {
					Log.wtf("Main", "Unitialize lib", e);
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
		}).start();

	}

	protected void load() {
		try {
			// initialize all library class.
			Bootstrap.Initialize(this, this);
		} catch (Exception ex) {
			Log.e("org.indiarose.frontend.PictoSelection",
					"Bootstrap Initialization error", ex);
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			android.os.Process.killProcess(android.os.Process.myPid());
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.onBackPressed();
			return true;
		}
		return false;
	}

	@Override
	public void onBootstrapInitialised() {
		ajouterVues();
		ajouterListeners();
	}

}