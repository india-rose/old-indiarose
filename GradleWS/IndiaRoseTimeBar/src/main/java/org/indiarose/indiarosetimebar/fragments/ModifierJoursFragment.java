package org.indiarose.indiarosetimebar.fragments;

import java.util.*;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.calendrier.CalendrierAndroidTimesSquare;
import org.indiarose.indiarosetimebar.calendrier.core.Calendrier;
import org.indiarose.indiarosetimebar.calendrier.core.Calendrier.DateChoisieDelegate;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.model.Periode;
import org.indiarose.indiarosetimebar.parametres.Parametres;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.os.Bundle;
import android.view.*;

/**
 * Fragment de moficiation des donnes de l'application
 * Permet de choisir des jours, et d'y ajouter des periodes, indiagrams, etc.
 * @author florentchampigny
 *
 */
public class ModifierJoursFragment extends BarreDeTempsFragmentNormal implements
		DateChoisieDelegate, View.OnClickListener {
	Bundle savedInstanceState = null;
	Parametres parametres;

	ViewGroup mainLayout;
	Calendrier calendar;
	List<Jour> joursChoisis;
	View bouton_valider;
	View bouton_retour;
	View bouton_creer_recurrences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.modifier_jours_layout)) {

			this.parametres = Parametres.getInstance(getActivity());

			ajouterVues();
			chargerVues();
			ajouterListener();

		}

		return getFragmentView();
	}

	@Override
	public void onResume() {
		super.onResume();
		ajouterVues();
		chargerVues();
		ajouterListener();
	}

	private void ajouterListener() {
		bouton_retour.setOnClickListener(this);
		bouton_valider.setOnClickListener(this);
		bouton_creer_recurrences.setOnClickListener(this);
	}

	private void chargerVues() {
		this.calendar = new CalendrierAndroidTimesSquare(getActivity(), this,
				mainLayout);
		bouton_valider.setVisibility(View.GONE);

		this.calendar.afficherJours(JourManager.joursNonVides(getDonnees().getJours()),
				parametres.getCouleurJoursAvecBarreDeTemps());
	}

	private void ajouterVues() {
		mainLayout = (ViewGroup) findViewById(R.id.modifier_jours_main_layout);
		bouton_valider = findViewById(R.id.modifier_jours_valider);
		bouton_retour = findViewById(R.id.modifier_bouton_retour);
		bouton_creer_recurrences = findViewById(R.id.modifier_jours_creer_recurrences);
	}

	@Override
	public void onDateChoisie(List<Date> selectedDates) {
		if (selectedDates.size() == 0) {
			joursChoisis = null;
			bouton_valider.setVisibility(View.GONE);
		} else {
			joursChoisis = JourManager.getLesJours(getDonnees(), selectedDates);
			bouton_valider.setVisibility(View.VISIBLE);
		}

	}

	public void lancerActionPeriodique() {
		ajouterFragment(new ActionPeriodiqueFragment());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.modifier_bouton_retour) {
			retirerFragment();
		} else if (id == R.id.modifier_jours_valider && joursChoisis != null) {
			// c'ets la qu'on regarde, si tous les jours sont egaux, ou tous
			// vides
			// si oui , on envoi sur BarreDeTempsFragments, pour modifier un
			// ensemble de jours,
			// sinon, on lance un fragment qui va permettre de garder certaines
			// periodes et fusionner les jours
			if (JourManager.tousEgaux(joursChoisis)
					|| JourManager.tousVides(joursChoisis)) {
				if (joursChoisis.get(0).getPeriodes() == null) {
					joursChoisis.get(0).setPeriodes(new ArrayList<Periode>());
				}
				ajouterFragment(new BarreDeTempsModifiableFragment(
						joursChoisis, joursChoisis.get(0).getPeriodes()));

			} else
				ajouterFragment(new SelectionnerPeriodesFragment(joursChoisis));
		} else if (id == R.id.modifier_jours_creer_recurrences) {
			lancerActionPeriodique();
		}
	}

}
