package org.indiarose.indiarosetimer.fragment;

import org.indiarose.indiarosetimer.modele.Categorie;

import org.indiarose.api.fragments.SelectionnerIndiagramFragment;
import org.indiarose.api.fragments.SelectionnerIndiagramFragment.SelectionnerIndiagramDelegate;
import org.indiarose.api.fragments.core.FragmentNormal;
import org.indiarose.lib.model.Indiagram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.indiarose.indiarosetimer.R;

import org.indiarose.indiarosetimer.database.base.AccessBaseTimer;

public class CreationCategorieFragment extends FragmentNormal implements View.OnClickListener,SelectionnerIndiagramDelegate{

	View _indiagram_categorie;
	View _style_categorie;
	View _valider;
	
	Categorie _categorie;

	private int choix_type_categorie = 0 ;
	private static String [] items ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(super.onCreateView(inflater, container, savedInstanceState,
				R.layout.creation_categorie)){

			ajouterVues();
			chargerVues();
			ajouterListener();
		}

		return getFragmentView();
	}

	private void chargerVues() {
		_categorie = new Categorie();
		items = new String[2];
		items[Categorie.TYPE_TIMER]=getString(R.string.timer);
		items[Categorie.TYPE_BARRE_TACHE]=getString(R.string.barre_tache);
	}

	private void ajouterListener() {
		_indiagram_categorie.setOnClickListener(this);
		_style_categorie.setOnClickListener(this);
		_valider.setOnClickListener(this);
	}

	private void ajouterVues() {
		_indiagram_categorie = findViewById(R.id.indiagram_categorie);		
		_style_categorie = findViewById(R.id.style_categorie);
		_valider = findViewById(R.id.valider);
	}

	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.indiagram_categorie:
			ajouterFragment(new SelectionnerIndiagramFragment(this),true);
			break;

		case R.id.style_categorie:
			changerTypeCategorie();
			break;
			
		case R.id.valider:
			enregistrerCategory();
			break;
		}
	}

	private void enregistrerCategory() {
		if(_categorie.getIndiagram() == null){
			Toast.makeText(getActivity(), "Veuillez choisir une image pour représenter la categorie", Toast.LENGTH_SHORT).show();
		}else{
			AccessBaseTimer db = new AccessBaseTimer(getActivity());
			db.open();
			db.insertCategory(_categorie);
			ajouterFragment(new GridCategoryFragment(),false);
			db.close();
		}
	}

	private void changerTypeCategorie() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle(getString(R.string.type));
		// set dialog message
		alertDialogBuilder
		.setSingleChoiceItems(items, choix_type_categorie, null)
		.setCancelable(false)
		.setPositiveButton(getString(R.string.valider),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				choix_type_categorie = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
				((TextView)_style_categorie.findViewById(R.id.tiret)).setText(items[choix_type_categorie]);

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
	public void onIndiagramSelected(Indiagram indiagram) {
		Log.e("IndiaRecu",indiagram.filePath);
		if(indiagram != null){

			_categorie.setIndiagram(indiagram);

			if(indiagram.text != null)
				((TextView)_indiagram_categorie.findViewById(R.id.titre)).setText(indiagram.text);

			if(indiagram.getImageAsBitmap() != null)
				((ImageView)_indiagram_categorie.findViewById(R.id.image)).setImageBitmap(indiagram.getImageAsBitmap());
			
		}
	}
	
	
	@Override
	public boolean onBackPressed(){
		super.onBackPressed();
		ajouterFragment(new GridCategoryFragment(),false);
		return true;
	}

}
