package org.indiarose.indiarosetimebar.views;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.views.core.ListElementView;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Vue qui apparait en bout de liste, permettant d'ajouter une element (si besoin)
 * Appelle le delegate de selection avec un objet vide lors du click (c'est a cela qu'on le reconnait)
 * @author florentchampigny
 */
public class AjouterView extends ListElementView {

	TextView titre;
	ImageView image;

	public AjouterView(Context context, View view, int position,
			ListElementViewClickDelegate delegate) {
		super(new Object(), context, view, position, delegate);
	}

	@Override
	public void ajouterVues() {
		titre = (TextView) getView().findViewById(R.id.titre);
		image = (ImageView) getView().findViewById(R.id.image);
	}

	@Override
	public void afficherNormal() {
		titre.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_selectionnee_text));
		image.setImageDrawable(getContext().getResources().getDrawable(
				R.drawable.plus));
	}

	@Override
	public void afficherTouch() {
		titre.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_text));
		image.setImageDrawable(getContext().getResources().getDrawable(
				R.drawable.plus_hover));
	}

	@Override
	public void remplirVues() {

	}

}
