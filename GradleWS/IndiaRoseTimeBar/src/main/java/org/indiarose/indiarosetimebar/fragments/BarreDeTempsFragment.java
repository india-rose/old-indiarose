package org.indiarose.indiarosetimebar.fragments;

import java.util.*;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.canvas.agenda.Agenda;
import org.indiarose.indiarosetimebar.canvas.horloge.*;
import org.indiarose.indiarosetimebar.canvas.horloge.core.Horloge;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.model.*;
import org.indiarose.indiarosetimebar.parametres.Parametres;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;

/**
 * Fragment permettant d'affucher un ChronoGram
 * Ce dernier n'est pas modifiable, juste consultable
 * 
 * A initialiser avec 1 ou plusieurs jours
 * Dans le cas de plusieurs jours, l'affichage de la semaine / de l'agenda est desactive
 * @author florentchampigny
 *
 */
@SuppressLint("ValidFragment")
public class BarreDeTempsFragment extends BarreDeTempsFragmentNormal {

	static protected ViewGroup _layoutHaut; //Layout du haut, cense contenir l'horloge
	static protected ViewGroup _layoutBas; //Layout du bas, cense contenir l'agenda

	static protected Bundle savedInstanceState = null;

	static protected Horloge horloge; //Zone d'affichage du Chronogram
	static protected Agenda agenda; //Zone de dessin des jours, qui va contenir l'affichage des jours de la semaine

	static protected Parametres parametres; //Parametres de l'application

	static protected Semaine semaine = null;

	static protected List<Jour> jours = new ArrayList<Jour>(); //Jours a afficher / modifier
	static protected List<Periode> periodes = new ArrayList<Periode>();

	static protected Jour jourTmp = null;
	static boolean modifiable = false;

	public BarreDeTempsFragment() {
	}

	public BarreDeTempsFragment(Jour jour) {
		BarreDeTempsFragment.periodes = jour.getPeriodes();
		BarreDeTempsFragment.jours = Arrays.asList(jour);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.barre_de_temps)) {

			BarreDeTempsFragment.parametres = Parametres.getInstance(getActivity());

			System.out.println(periodes);

			chargerSemaineEtJour();

			ajouterVues();
			chargerVues();
			ajouterListener();
		}

		return getFragmentView();
	}

	protected void ajouterVues() {
		_layoutHaut = (ViewGroup) findViewById(R.id.barre_temps_layout_haut);
		_layoutBas = (ViewGroup) findViewById(R.id.barre_temps_layout_bas);
	}

	protected void ajouterListener() {
	}

	protected void chargerVues() {
		afficherHorloge();

		if (parametres.isAfficherJours() && jours != null && jours.size() == 1) {
			creerAgenda();
			
			//Calcule la taille des elements en fonction de l'ecran
			agenda.calculerMeilleurTaille();
			
			//Puis l'ajoute dans le layout du bas
			_layoutBas.addView(agenda);
		} else {
			_layoutBas.setVisibility(View.GONE);
		}
	}

	/**
	 * Cree l'agenda et le place dans le layout du bas
	 */
	protected void creerAgenda() {
		agenda = new Agenda(getActivity(), semaine, getIndiagramManager());
	}
	
	/**
	 * Charge la semaine en fonction du jour 
	 * Voir JourManager.getSemaineDeCetteDate
	 */
	protected void chargerSemaineEtJour() {
		if (jours != null && jours.size() == 1) {
			BarreDeTempsFragment.semaine = JourManager.getSemaineDeCetteDate(getDonnees(),
					jours.get(0).getDate());
		}
	}

	/**
	 * Cree l'horloge et la place dans le layout du haut
	 */
	protected void afficherHorloge() {

		//Si l'horloge a deja ete cree, La remplace
		try {
			horloge.getThread().interrupt();
		} catch (Exception e) {
		}

		_layoutHaut.removeAllViews();

		// Instancie l'horloge en fonction de son type
		TypeHorloge type = parametres.getType();

		
		if (type.equals(TypeHorloge.CERCLE))
			horloge = new Cercle(getActivity(), periodes, modifiable,
					getIndiagramManager());
		else if (type.equals(TypeHorloge.BARRE_HORIZONTALE))
			horloge = new BarreHorizontale(getActivity(), periodes, modifiable,
					getIndiagramManager());
		else if (type.equals(TypeHorloge.BARRE_VERTICALE))
			horloge = new BarreVerticale(getActivity(), periodes, modifiable,
					getIndiagramManager());

		//Calcule la taille des elements en fonction de l'ecran
		horloge.calculerMeilleurTaille();

		//Puis l'ajoute dans le layout du haut
		_layoutHaut.addView(horloge);
	}
}
