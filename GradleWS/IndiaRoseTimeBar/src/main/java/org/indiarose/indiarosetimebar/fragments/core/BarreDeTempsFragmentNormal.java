package org.indiarose.indiarosetimebar.fragments.core;

import org.indiarose.api.fragments.core.FragmentNormal;
import org.indiarose.indiarosetimebar.activity.MainActivity;
import org.indiarose.indiarosetimebar.model.Donnees;
import org.indiarose.indiarosetimebar.persistance.Persistance;

/**
 * Surcouche du FragmentNormal permettant d'y ajouter des fonctions propres au ChronoGram
 * @author florentchampigny
 *
 */
public abstract class BarreDeTempsFragmentNormal extends FragmentNormal {

	public Donnees getDonnees() {
		return ((MainActivity) getActivity()).getDonnees();
	}

	public Persistance getPersistance() {
		return ((MainActivity) getActivity()).getPersistance();
	}

}
