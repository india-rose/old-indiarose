package org.indiarose.api.fragments;

import java.util.List;

import org.indiarose.R;
import org.indiarose.api.adapter.IndiagramListAdapter;
import org.indiarose.api.fragments.core.FragmentNormal;
import org.indiarose.api.views.IndiagramView.IndiagramViewClickDelegate;
import org.indiarose.lib.model.Category;
import org.indiarose.lib.model.Indiagram;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;
import android.widget.AbsListView;

/**
 * This fragment can be used to ask an Indiagram selection to the current user
 * The result is returned vie the SelectionnerIndiagramDelegate'function onIndiagramSelected(Indiagram indiagram);
 * @author florentchampigny
 */
@SuppressLint("ValidFragment")
public class SelectionnerIndiagramFragment extends FragmentNormal implements
		View.OnClickListener, IndiagramViewClickDelegate {
	
	/**
	 * Delegate which is called when an indiagram is choosen
	 * @author florentchampigny
	 *
	 */
	public interface SelectionnerIndiagramDelegate {
		public void onIndiagramSelected(Indiagram indiagram);
	}
	
	Bundle savedInstanceState = null;

	AbsListView list;
	IndiagramListAdapter adapter;

	static SelectionnerIndiagramDelegate delegate;
	static Category categorie = null;
	
	//nombre de pages/fragments a retirer une fois le choix effectue
	int nombre = 1;

	

	public SelectionnerIndiagramFragment() {
		nombre = 1;
	}

	public SelectionnerIndiagramFragment(SelectionnerIndiagramDelegate delegate) {
		this.delegate = delegate;
	}

	public SelectionnerIndiagramFragment(
			SelectionnerIndiagramDelegate delegate, Category categorie, int nombre) {
		this(delegate);
		this.categorie = categorie;
		this.nombre = nombre;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		onCreateView(inflater, container, savedInstanceState,
				R.layout.selection_indiagrams);

		ajouterVues();
		chargerVues();
		ajouterListener();

		return getFragmentView();
	}

	private void ajouterVues() {
		list = (AbsListView) findViewById(android.R.id.list);
	}

	@SuppressLint("NewApi")
	private void chargerVues() {
		List<Indiagram> indiagrams = null;
		if (categorie != null) {
			indiagrams = getIndiagramManager().getIndiagramsOfCategory(
					categorie);
		} else {
			indiagrams = getIndiagramManager().getRootCategoriesaAsIndiagrams();
		}
		adapter = new IndiagramListAdapter(getActivity(), indiagrams, this);
		list.setAdapter(adapter);
	}

	private void ajouterListener() {
	}

	@Override
	public void onIndiagramViewClick(Indiagram indiagram) {
		if (indiagram instanceof Category) {
			ajouterFragment(new SelectionnerIndiagramFragment(delegate,
					(Category) indiagram, nombre+1));
		} else {
			delegate.onIndiagramSelected(indiagram);
			retirerFragment(nombre);
			categorie = null;
			delegate = null;
		}
	}
}
