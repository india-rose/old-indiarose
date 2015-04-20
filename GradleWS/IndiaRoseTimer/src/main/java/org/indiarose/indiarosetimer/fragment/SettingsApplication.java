package org.indiarose.indiarosetimer.fragment;

import org.indiarose.api.fragments.core.FragmentNormal;

import org.indiarose.indiarosetimer.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsApplication extends FragmentNormal{

	private static final int ORIENTATION_PORTRAIT = 0;
	private static final int ORIENTATION_LANDSCAPE = 1;
	
	View _orientation_application;
	View _valider;
	SharedPreferences pref ; // 0 - for private mode
	Editor editor ;
	
	private static String [] items ;

	private int choix_orientation_application ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.settings_application)){
			ajouterVues();
			ajouterListeners();
			chargerVues();
		}
		return getFragmentView();
	}

	private void chargerVues() {
		items = new String[2];
		items[0] = getString(R.string.portrait);
		items[1] = getString(R.string.paysage);
		
		pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
		editor =  pref.edit();
		
		int screen_orientation = pref.getInt("screen_orientation", -1);

		if(screen_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ){
			choix_orientation_application = ORIENTATION_PORTRAIT;
		}else{
			choix_orientation_application = ORIENTATION_LANDSCAPE;
		}
		
		((TextView)_orientation_application.findViewById(R.id.tiret)).setText(items[choix_orientation_application]);

	}

	private void ajouterListeners() {
		_orientation_application.setOnClickListener(this);
		_valider.setOnClickListener(this);
	}

	private void ajouterVues() {
		_orientation_application = findViewById(R.id.orientation_application);
		_valider = findViewById(R.id.valider_application);
	}


	public void onClick(View v){
		switch (v.getId()) {
		case R.id.orientation_application:
			changerOrientation();
			break;

		case R.id.valider_application:
			enregistreParametreApplication();
			break;
		}
	}


	private void enregistreParametreApplication() {


		if(choix_orientation_application == ORIENTATION_PORTRAIT)
			editor.putInt("screen_orientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else
			editor.putInt("screen_orientation",ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


		editor.commit(); 

		ajouterFragment(new Home(),false);		
	}

	public void changerOrientation(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle(getString(R.string.orientation_application));
		// set dialog message
		alertDialogBuilder
		.setSingleChoiceItems(items, choix_orientation_application, null)
		.setCancelable(true)
		.setPositiveButton(getString(R.string.valider),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				choix_orientation_application = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
				((TextView)_orientation_application.findViewById(R.id.tiret)).setText(items[choix_orientation_application]);

			}
		})
		.setNegativeButton(getString(R.string.annuler),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}
	
	@Override
	public boolean onBackPressed(){
		super.onBackPressed();
		ajouterFragment(new Home(),false);
		return true;
	}
}
