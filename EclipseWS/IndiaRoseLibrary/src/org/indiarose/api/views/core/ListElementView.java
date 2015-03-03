package org.indiarose.api.views.core;

import org.indiarose.R;

import android.content.Context;
import android.view.*;
import android.view.View.OnClickListener;

public abstract class ListElementView implements IListElementView,
		OnClickListener, View.OnTouchListener {

	Context context;
	View view;

	Object objet;
	int position;
	boolean selected = false;

	public ListElementView(Object objet, Context context, View view,
			int position) {
		this.context = context;
		this.view = view;
		this.position = position;
		this.objet = objet;

		ajouterVues();
		remplirVues();
		ajouterListeners();

		afficherNormal();
	}

	@Override
	public void ajouterListeners() {
		getView().setOnClickListener(this);
		getView().setOnTouchListener(this);
	}

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

	public void afficherTouch() {
		getView().setBackgroundColor(
				getContext().getResources().getColor(R.color.vert));
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
		}
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
