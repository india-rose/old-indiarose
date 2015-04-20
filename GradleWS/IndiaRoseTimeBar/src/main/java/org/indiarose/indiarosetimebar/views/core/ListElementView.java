package org.indiarose.indiarosetimebar.views.core;

import org.indiarose.indiarosetimebar.R;

import android.content.Context;
import android.view.*;
import android.view.View.OnClickListener;

/**
 * Gere l'affichage d'un element d'une listview, ainsi que sa coloration lors de la selection, et l'appel au delegate
 * @author florentchampigny
 */
public abstract class ListElementView implements IListElementView,
		OnClickListener, View.OnTouchListener, View.OnLongClickListener {

	/**
	 * Delegate appelle lors de la selection d'un element
	 * @author florentchampigny
	 */
	public interface ListElementViewClickDelegate {
		public void onListElementViewClicked(Object objet);

		public void onListElementViewLongClicked(Object objet);
	}

	Context context;
	View view;

	Object objet; //Objet donnee
	int position; //position de l'element
	boolean selected = false;
	ListElementViewClickDelegate delegate;

	public ListElementView(Object objet, Context context, View view,
			int position, ListElementViewClickDelegate delegate) {
		this.context = context;
		this.view = view;
		this.position = position;
		this.objet = objet;
		this.delegate = delegate;

		ajouterVues();
		remplirVues();
		ajouterListeners();

		afficherNormal();
	}

	@Override
	public void ajouterListeners() {
		getView().setOnClickListener(this);
		getView().setOnTouchListener(this);
		if (delegate != null)
			getView().setOnLongClickListener(this);
	}

	/**
	 * Affichage "normal", donc lors de la creation
	 */
	public void afficherNormal() {
		if (getPosition() % 2 == 0) {
			getView().setBackgroundColor(
					getContext().getResources().getColor(
							R.color.couleur_cellule_paire));
		} else {
			getView().setBackgroundColor(
					getContext().getResources().getColor(
							R.color.couleur_cellule_impaire));
		}
	}

	/**
	 * Affichage de la cellule lors du Touch
	 */
	public void afficherTouch() {
		getView().setBackgroundColor(
				getContext().getResources().getColor(
						R.color.couleur_cellule_selectionnee));
	}

	public void afficherLongClicked() {
		getView().setBackgroundColor(
				getContext().getResources().getColor(
						R.color.couleur_cellule_long_clicked));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			setSelected(!isSelected());
			if (isSelected()) {
				afficherTouch();
			} else
				afficherNormal();
			if (delegate != null)
				delegate.onListElementViewClicked(this.objet);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		default:
			if (delegate != null) {
				if (selected) {
					afficherNormal();
					this.setSelected(false);
				} else {
					afficherLongClicked();
					this.setSelected(true);
				}

				delegate.onListElementViewLongClicked(this.objet);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			afficherTouch();
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			afficherNormal();
		}
		return false;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void reCharger(View view) {
		setView(view);
		ajouterVues();
		remplirVues();
		ajouterListeners();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Object getObjet() {
		return objet;
	}

	public void setObjet(Object objet) {
		this.objet = objet;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
