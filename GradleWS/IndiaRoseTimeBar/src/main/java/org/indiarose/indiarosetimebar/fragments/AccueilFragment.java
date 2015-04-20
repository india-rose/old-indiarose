package org.indiarose.indiarosetimebar.fragments;

import java.util.Date;
import java.util.List;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.calendrier.CalendrierAndroidCalendarCard;
import org.indiarose.indiarosetimebar.calendrier.core.Calendrier;
import org.indiarose.indiarosetimebar.calendrier.core.Calendrier.DateChoisieDelegate;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.parametres.Parametres;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.os.Bundle;
import android.view.*;

/**
 * Fragment affiche lors du lancement de l'application ChronoGram
 * Affiche le calendrier, un lien vers les preferences
 * Et permet d'aller vers la modification, ou l'interface de consultation
 * @author florentchampigny
 *
 */
public class AccueilFragment extends BarreDeTempsFragmentNormal implements
		DateChoisieDelegate, View.OnClickListener {

	Bundle savedInstanceState = null;
	Parametres parametres; //Les parametres de l'application

	ViewGroup mainLayout; //La vue centrale
	View modifier; //Le bouton modifier
	View consulter; //Le bouton consulter
	View preferences; //Le bouton de preferences

	Calendrier calendar; //Le calendrier
	Jour jourChoisi; //Le jour choisi sur le calendrier

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.accueil_layout)) {

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

		this.calendar.afficherJours(getDonnees().getJours(),
		this.parametres.getCouleurJoursAvecBarreDeTemps());
	}

	private void ajouterListener() {
		modifier.setOnClickListener(this);
		consulter.setOnClickListener(this);
		preferences.setOnClickListener(this);
	}

	private void chargerVues() {
		this.calendar = new CalendrierAndroidCalendarCard(getActivity(), this,
				mainLayout);

		this.calendar.afficherJours(JourManager.joursNonVides(getDonnees().getJours()),
				parametres.getCouleurJoursAvecBarreDeTemps());

		consulter.setVisibility(View.GONE);
	}

	private void ajouterVues() {
		mainLayout = (ViewGroup) findViewById(R.id.modifier_jours_main_layout);
		modifier = findViewById(R.id.accueil_bouton_modifier);
		consulter = findViewById(R.id.accueil_bouton_consulter);
		preferences = findViewById(R.id.accueil_bouton_preferences);
	}

	@Override
	public void onDateChoisie(List<Date> dates) {
		if (dates != null && dates.size() > 0) {
			jourChoisi = JourManager.getJour(getDonnees(), dates.get(0));
			consulter.setVisibility(View.VISIBLE);

		} else {
			jourChoisi = null;

			consulter.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.accueil_bouton_modifier) {
			lancerModif();
		} else if (id == R.id.accueil_bouton_consulter && jourChoisi != null) {
			lancerConsult();
		} else if (id == R.id.accueil_bouton_preferences) {
			lancerParametres();
		}
	}

	private void lancerModif() {
		ajouterFragment(new ModifierJoursFragment());
	}

	private void lancerConsult() {
		if (jourChoisi != null) {
			ajouterFragment(new BarreDeTempsFragment(jourChoisi));
		}

	}

	private void lancerParametres() {
		ajouterFragment(new PreferencesFragment());
	}
}
