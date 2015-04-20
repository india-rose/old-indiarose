package org.indiarose.indiarosetimebar.views;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.activity.MainActivity;
import org.indiarose.indiarosetimebar.model.Periode;
import org.indiarose.indiarosetimebar.views.core.ListElementView;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Implementation d'une ListElementView permettant d'y afficher une periode
 * @author florentchampigny
 *
 */
public class PeriodeView extends ListElementView {

	TextView titre;
	TextView hautDroite;
	TextView basDroite;
	ImageView image;

	public PeriodeView(Object objet, Context context, View view, int position,
			ListElementViewClickDelegate delegate) {
		super(objet, context, view, position, delegate);
	}

	@Override
	public void ajouterVues() {
		titre = (TextView) getView().findViewById(R.id.titre);
		hautDroite = (TextView) getView().findViewById(R.id.haut_droite);
		basDroite = (TextView) getView().findViewById(R.id.bas_droite);
		image = (ImageView) getView().findViewById(R.id.image);
	}

	@Override
	public void remplirVues() {
		if (getObjet() instanceof Periode) {
			Periode periode = (Periode) getObjet();
			if (periode != null) {
				if (periode.getNom() != null)
					titre.setText(periode.getNom());
				if (periode.getHeureDebut() != null)
					hautDroite.setText(periode.getHeureDebut().toString());
				if (periode.getHeureFin() != null)
					basDroite.setText(periode.getHeureFin().toString());
				if (periode.getIndiagramPath() != null
						&& getContext() instanceof MainActivity
						&& (periode
								.chargerIndiagram(((MainActivity) getContext())
										.getIndiagramManager()))) {
					image.setImageBitmap(periode.getIndiagramBitmap());
				} else
					image.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void afficherNormal() {
		super.afficherNormal();
		titre.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_text));
		hautDroite.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_text));
		basDroite.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_text));
	}

	@Override
	public void afficherTouch() {
		super.afficherTouch();
		titre.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_selectionnee_text));
		hautDroite.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_selectionnee_text));
		basDroite.setTextColor(getContext().getResources().getColor(
				R.color.couleur_cellule_selectionnee_text));
	}

}
