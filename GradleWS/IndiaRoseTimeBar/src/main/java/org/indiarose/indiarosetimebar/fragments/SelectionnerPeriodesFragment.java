package org.indiarose.indiarosetimebar.fragments;

import java.util.List;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.adapter.SelectionListAdapter;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.model.Periode;
import org.indiarose.indiarosetimebar.utils.JourManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
/**
 * Est appelle lors de la selection de plusieurs jours, ayant des periodes differentes
 * Permet de faire le tri et choisir les periodes a garder pour la suite
 * @author florentchampigny
 *
 */
@SuppressLint("ValidFragment")
public class SelectionnerPeriodesFragment extends BarreDeTempsFragmentNormal
		implements View.OnClickListener {
	Bundle savedInstanceState = null;

	List<Jour> jours;
	List<Periode> periodes;
	ListView list;
	SelectionListAdapter<Periode> adapter;

	View valider = null;

	public SelectionnerPeriodesFragment(List<Jour> jours) {
		this.periodes = JourManager.joursToPeriodes(jours);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.selectionner_periodes)) {

			ajouterVues();
			chargerVues();
			ajouterListener();

		}

		return getFragmentView();
	}

	private void ajouterVues() {
		list = (ListView) findViewById(android.R.id.list);
		valider = findViewById(R.id.valider);
	}

	private void chargerVues() {
		adapter = new SelectionListAdapter<Periode>(getActivity(), periodes);
		list.setAdapter(adapter);
	}

	private void ajouterListener() {
		valider.setOnClickListener(this);
	}

	private void valider() {
		List<Periode> periodes = adapter.getSelectedObjects();
		ajouterFragment(new BarreDeTempsModifiableFragment(jours, periodes));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.valider)
			valider();
	}

}
