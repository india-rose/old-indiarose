package org.indiarose.indiarosetimebar.adapter;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.views.AjouterView;
import org.indiarose.indiarosetimebar.views.PeriodeView;
import org.indiarose.indiarosetimebar.views.core.ListElementView;
import org.indiarose.indiarosetimebar.views.core.ListElementView.ListElementViewClickDelegate;

import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;

/**
 * Permet d'afficher une liste d'elements selectionnables, a partir de l'importe quel objet
 * @author florentchampigny
 *
 * @param <T> Type de l'objet donnees
 */
public class SelectionListAdapter<T> extends BaseAdapter {

	private Context context;
	private List<T> objets; //Objets donnees
	private List<ListElementView> views = new ArrayList<ListElementView>(); //vues
	private ListElementViewClickDelegate delegate = null; //Delegate qui va etre appelle lors de la selection d'une vue
	private boolean afficherAjouter; //vrai si on affiche ajouter en bas de la liste

	private static LayoutInflater inflater = null;

	public SelectionListAdapter(Context context, List<T> objets) {
		this(context, objets, null, false);
	}

	public SelectionListAdapter(Context context, List<T> objets,
			ListElementViewClickDelegate delegate, boolean afficherAjouter) {
		this.context = context;
		this.objets = objets;
		this.delegate = delegate;
		this.afficherAjouter = afficherAjouter;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (objets != null) {
			int nb = objets.size();
			if (afficherAjouter)
				return nb + 1;
			return nb;
		} else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return objets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ListElementView getView(int position) {
		return views.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if (afficherAjouter && position == getCount() - 1) {
			view = inflater.inflate(R.layout.selection_listview_ajouter, null);

			AjouterView sv = new AjouterView(context, view, position, delegate);
			views.add(position, sv);
		} else {
			view = inflater.inflate(R.layout.selection_listview_element, null);

			PeriodeView sv = new PeriodeView(objets.get(position), context,
					view, position, delegate);
			views.add(position, sv);
		}

		return view;
	}

	/**
	 * Retourne la liste des vues selectionnes
	 * @return la liste des vues selectionnes
	 */
	public List<ListElementView> getSelectedViews() {
		List<ListElementView> vs = new ArrayList<ListElementView>();
		for (ListElementView v : views)
			if (v.isSelected())
				vs.add(v);

		return vs;
	}

	/**
	 * Retourne la liste des objets selectionnes
	 * @return la liste des objets selectionnes
	 */
	@SuppressWarnings("unchecked")
	public List<T> getSelectedObjects() {
		List<T> objets = new ArrayList<T>();
		for (ListElementView v : views)
			if (v.isSelected())
				objets.add((T) v.getObjet());

		return objets;
	}

}
