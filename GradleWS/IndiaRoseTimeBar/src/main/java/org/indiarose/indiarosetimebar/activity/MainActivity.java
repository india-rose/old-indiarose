package org.indiarose.indiarosetimebar.activity;

import org.indiarose.api.IndiagramManager;
import org.indiarose.api.activity.IndiaRoseFragmentActivity;
import org.indiarose.indiarosetimebar.fragments.AccueilFragment;
import org.indiarose.indiarosetimebar.model.Donnees;
import org.indiarose.indiarosetimebar.parametres.Parametres;
import org.indiarose.indiarosetimebar.persistance.Persistance;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;

/**
 * Activite principale du ChronoGram
 * Herite de IndiaRoseFragmentActivity pour la gestion de fragments
 * 
 * @author florentchampigny
 *
 * Va gerer la persistance des donnees, et les parametres de l'application
 */
public class MainActivity extends IndiaRoseFragmentActivity implements
		OnBackStackChangedListener, IndiagramManager.IndiagramManagerDelegate {

	static Persistance persistance = null;
	static Donnees donnees = null;
	static Parametres parametres = null;

	@Override
	public void onIndiagramManagerInitialised() {

		if (donnees == null && persistance == null) {
			persistance = Persistance.getInstance(this);
			donnees = persistance.chargerDonnees();
			parametres = Parametres.getInstance(this);
		}
		
		setRequestedOrientation(parametres.getOrientation());

		ajouterVues();
		charger();
		ajouterListeners();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//if (persistance != null && donnees != null)
		//	persistance.sauvegarder(donnees);
	}

	protected void ajouterVues() {
	}

	protected void charger() {
		// affichage initial

		if (getFragments().size() == 0)
			ajouterFragment(new AccueilFragment());
		else {
			Fragment f = getFragments().getLast();
			if (f != null){
				try{
				f.onResume();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public Persistance getPersistance() {
		return persistance;
	}

	public Donnees getDonnees() {
		return donnees;
	}

}