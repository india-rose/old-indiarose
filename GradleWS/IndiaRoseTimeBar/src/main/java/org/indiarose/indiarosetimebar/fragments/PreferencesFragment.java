package org.indiarose.indiarosetimebar.fragments;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.canvas.horloge.TypeHorloge;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.parametres.Parametres;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

/**
 * Fragment permettant de gerer/modifier/sauvegarder les parametres de l'application
 * @author florentchampigny
 *
 */
public class PreferencesFragment extends BarreDeTempsFragmentNormal implements
View.OnClickListener {

	Bundle savedInstanceState = null;
	Parametres parametres;

	protected enum CouleurPour {
		AUJOURDHUI, JOURS, JOURS_AVEC_BARRE_DE_TEMPS
	};

	protected enum OuiNonPour {
		AFFICHER_FOND, AFFICHER_JOURS, FOND_DYNAMIQUE
	};

	String[] orientations;

	final int[] orientationsValeurs = new int[]{
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
	};

	View _format;
	View _orientation;
	View _afficherFond;
	View _afficherJours;
	View _fondDynamique;
	View _couleurAujourdui;
	View _couleurJours;
	View _couleurJoursAvecBarreDeTemps;

	boolean active = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.parametres)) {

			orientations = new String[]{
					getActivity().getResources().getString(R.string.portrait),
					getActivity().getResources().getString(R.string.paysage)
			};

			this.parametres = Parametres.getInstance(getActivity());

			ajouterVues();
			chargerVues();
			ajouterListener();

		}

		return getFragmentView();
	}

	protected void ajouterVues() {
		_format = findViewById(R.id.format);
		_orientation = findViewById(R.id.orientation);
		_afficherFond = findViewById(R.id.afficher_fond);
		_afficherJours = findViewById(R.id.afficher_les_jours);
		_fondDynamique = findViewById(R.id.fond_horloge_dynamique);
		_couleurAujourdui = findViewById(R.id.couleur_aujourdhui);
		_couleurJours = findViewById(R.id.couleur_jours);
		_couleurJoursAvecBarreDeTemps = findViewById(R.id.couleur_jours_avec_barre_de_temps);

	}

	protected String afficherOuiNon(boolean b) {
		return b ? getString(R.string.oui) : getString(R.string.non);
	}

	protected void chargerVues() {
		((TextView) _format.findViewById(R.id.valeur)).setText(parametres
				.getType().getName());
		((TextView) _afficherFond.findViewById(R.id.valeur))
		.setText(afficherOuiNon(parametres.isAfficherFond()));
		((TextView) _afficherJours.findViewById(R.id.valeur))
		.setText(afficherOuiNon(parametres.isAfficherJours()));
		((TextView) _fondDynamique.findViewById(R.id.valeur))
		.setText(afficherOuiNon(parametres
				.isDessinerFondHeureDynamique()));
		_couleurAujourdui.findViewById(R.id.valeur).setBackgroundColor(
				parametres.getCouleurAujourdhui());
		_couleurJours.findViewById(R.id.valeur).setBackgroundColor(
				parametres.getCouleurJours());
		_couleurJoursAvecBarreDeTemps.findViewById(R.id.valeur)
		.setBackgroundColor(
				parametres.getCouleurJoursAvecBarreDeTemps());

		for(int i=0;i<orientations.length; ++i)
			if(parametres.getOrientation() == orientationsValeurs[i]){
				((TextView) _orientation.findViewById(R.id.valeur))
				.setText(orientations[i]);
			}




	}

	protected void ajouterListener() {
		_format.setOnClickListener(this);
		_orientation.setOnClickListener(this);
		_couleurAujourdui.setOnClickListener(this);
		_couleurJours.setOnClickListener(this);
		_fondDynamique.setOnClickListener(this);
		_afficherFond.setOnClickListener(this);
		_afficherJours.setOnClickListener(this);
		_couleurJoursAvecBarreDeTemps.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.format:
			demanderType();
			break;
		case R.id.orientation:
			demanderOrientation();
			break;
		case R.id.afficher_fond:
			demanderAfficherFond();
			break;
		case R.id.fond_horloge_dynamique:
			demanderFondDynamique();
			break;
		case R.id.afficher_les_jours:
			demanderAfficherJours();
			break;
		case R.id.couleur_aujourdhui:
			demanderCouleurAujourdhui();
			break;
		case R.id.couleur_jours:
			demanderCouleurJours();
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		parametres.sauvegarder();
	}

	private void demanderType() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.format).setSingleChoiceItems(
				TypeHorloge.typesNames, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						parametres.setType(TypeHorloge.types[which]);
						chargerVues();
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void demanderOrientation(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



		builder.setTitle(R.string.orientation_appliquee_demarrage)
		.setSingleChoiceItems(
				orientations, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						parametres.setOrientation(orientationsValeurs[which]);
						chargerVues();
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void demanderOuiNon(String titre, final OuiNonPour ouiNonPour) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(titre)
		.setSingleChoiceItems(
				new String[] { getString(R.string.oui),
						getString(R.string.non) }, -1,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						boolean b = which == 0;
						if (ouiNonPour == OuiNonPour.AFFICHER_FOND)
							parametres.setAfficherFond(b);
						else if (ouiNonPour == OuiNonPour.AFFICHER_JOURS)
							parametres.setAfficherJours(b);
						else if (ouiNonPour == OuiNonPour.FOND_DYNAMIQUE)
							parametres.setDessinerFondHeureDynamique(b);

						chargerVues();
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void demanderAfficherJours() {
		demanderOuiNon(getString(R.string.afficher_les_jours),
				OuiNonPour.AFFICHER_JOURS);
	}

	private void demanderAfficherFond() {
		demanderOuiNon(getString(R.string.afficher_fond),
				OuiNonPour.AFFICHER_FOND);
	}

	private void demanderFondDynamique() {
		demanderOuiNon(getString(R.string.dessiner_fond_horloge_dynamique),
				OuiNonPour.FOND_DYNAMIQUE);
	}

	protected void demanderCouleurAujourdhui() {
		demanderCouleur(CouleurPour.AUJOURDHUI);
	}

	protected void demanderCouleurJours() {
		demanderCouleur(CouleurPour.JOURS);
	}

	protected void demanderCouleurJoursAvecBarreDeTemps() {
		demanderCouleur(CouleurPour.JOURS_AVEC_BARRE_DE_TEMPS);
	}

	protected void demanderCouleur(final CouleurPour couleurPour) {

		int color = 0;
		if (couleurPour == CouleurPour.AUJOURDHUI) {
			color = parametres.getCouleurAujourdhui();
		} else if (couleurPour == CouleurPour.JOURS) {
			color = parametres.getCouleurJours();
		} else if (couleurPour == CouleurPour.JOURS_AVEC_BARRE_DE_TEMPS) {
			color = parametres.getCouleurJours();
		}

		if (!active) {
			active = true;
			final afzkl.development.colorpickerview.dialog.ColorPickerDialog colorDialog = new afzkl.development.colorpickerview.dialog.ColorPickerDialog(
					getActivity(), color);

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.choisir_color));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (couleurPour == CouleurPour.AUJOURDHUI) {
						parametres.setCouleurAujourdhui(colorDialog
								.getColor());
					} else if (couleurPour == CouleurPour.JOURS) {
						parametres.setCouleurJours(colorDialog
								.getColor());
					} else if (couleurPour == CouleurPour.JOURS_AVEC_BARRE_DE_TEMPS) {
						parametres
						.setCouleurJoursAvecBarreDeTemps(colorDialog
								.getColor());
					}
					chargerVues();
					active = false;
				}
			});

			colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
					getString(android.R.string.cancel),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Nothing to do here.
					active = false;
				}
			});

			colorDialog.show();
		}

	}

}
