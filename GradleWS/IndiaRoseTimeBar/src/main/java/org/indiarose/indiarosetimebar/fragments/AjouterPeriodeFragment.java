package org.indiarose.indiarosetimebar.fragments;

import org.indiarose.api.fragments.SelectionnerIndiagramFragment;
import org.indiarose.api.fragments.SelectionnerIndiagramFragment.SelectionnerIndiagramDelegate;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.model.Heure;
import org.indiarose.indiarosetimebar.model.Periode;
import org.indiarose.lib.model.Indiagram;

import org.indiarose.indiarosetimebar.R;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

/**
 * Fragment appelle dans le cas d'une creation de periode
 * Retourne la periode creee via le delegate AjouterPeriodeFragmentDelegate.onPeriodeAjoutee(Periode periode);
 * @author florentchampigny
 */
@SuppressLint("ValidFragment")
public class AjouterPeriodeFragment extends BarreDeTempsFragmentNormal
		implements SelectionnerIndiagramDelegate 
	{
	
	/**
	 * Delegate utilise pour retourner la periode creee
	 * @author florentchampigny
	 *
	 */
	public interface AjouterPeriodeFragmentDelegate {
		public void onPeriodeAjoutee(Periode periode);
	}

	AjouterPeriodeFragmentDelegate delegate;

	boolean active = false;

	static Periode periode; //static dans le cas de changement d'orientation de l'ecran
	View _heureDebut;
	View _heureFin;
	View _nom;
	View _afficherCouleur;
	View _couleur;
	View _indiagram;
	View _envoyer;

	boolean afficherFond = false;

	boolean pourHeureDebut = true;

	public AjouterPeriodeFragment() {
	}

	public AjouterPeriodeFragment(AjouterPeriodeFragmentDelegate delegate) {
		this.delegate = delegate;
		periode = new Periode();
	}

	public AjouterPeriodeFragment(AjouterPeriodeFragmentDelegate delegate,
			float heure) {
		this(delegate);
		periode = new Periode();
		periode.setHeureDebut(new Heure(heure - 1));
		periode.setHeureFin(new Heure(heure + 1));
	}

	public AjouterPeriodeFragment(AjouterPeriodeFragmentDelegate delegate,
			Periode periode) {
		this(delegate);
		AjouterPeriodeFragment.periode = periode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (super.onCreateView(inflater, container, savedInstanceState,
				R.layout.ajouter_periode)) {

			ajouterVues();
			chargerVues();
			ajouterListener();
		}

		return getFragmentView();
	}

	protected void ajouterVues() {
		_heureDebut = findViewById(R.id.heure_debut);
		_heureFin = findViewById(R.id.heure_fin);
		_nom = findViewById(R.id.nom);
		_couleur = findViewById(R.id.couleur);
		_indiagram = findViewById(R.id.indiagram);
		_envoyer = findViewById(R.id.envoyer);
		_afficherCouleur = findViewById(R.id.afficher_couleur_de_fond);
	}

	protected void chargerVues() {
		((TextView) _heureDebut.findViewById(R.id.valeur)).setText(periode
				.getHeureDebut().toString());
		((TextView) _heureFin.findViewById(R.id.valeur)).setText(periode
				.getHeureFin().toString());
		_couleur.findViewById(R.id.valeur).setBackgroundColor(
				periode.getCouleur());

		if (periode.getNom().length() > 0)
			((TextView) _nom.findViewById(R.id.valeur)).setText(periode
					.getNom());

		_couleur.setVisibility(View.GONE);
		
		chargerIndiagram();
	}

	/**
	 * Permet de charger l'indiagram dans l'imageview correspondante
	 */
	protected void chargerIndiagram() {
		if (periode.chargerIndiagram(getIndiagramManager(),true)) {

			((ImageView) _indiagram.findViewById(R.id.image))
					.setImageBitmap(periode.getIndiagramBitmap());
			if (periode.getIndiagram().text != null)
				((TextView) _indiagram.findViewById(R.id.titre))
						.setText(periode.getIndiagram().text);
		}

		if (periode.getNom() != null && periode.getNom().length() > 0) {
			((Button) this._envoyer).setText(getActivity().getResources()
					.getString(R.string.modifier_l_evenement));
		}
	}

	protected void ajouterListener() {
		_heureDebut.setOnClickListener(this);
		_heureFin.setOnClickListener(this);
		_couleur.setOnClickListener(this);
		_envoyer.setOnClickListener(this);
		_afficherCouleur.setOnClickListener(this);
		_indiagram.findViewById(R.id.image).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.heure_debut) {
			changerHeure(true);
		} else if (id == R.id.heure_fin) {
			changerHeure(false);
		} else if (id == R.id.couleur) {
			changerCouleur();
		} else if (id == R.id.image) {
			changerImage();
		} else if (id == R.id.envoyer) {
			envoyer();
		} else if (id == R.id.afficher_couleur_de_fond) {
			switchAfficherouleurDeFond();
		}
	}

	@SuppressWarnings("deprecation")
	protected void switchAfficherouleurDeFond() {
		int ressourcesId = (afficherFond = !afficherFond) ? R.drawable.check
				: R.drawable.uncheck;

		_afficherCouleur.findViewById(R.id.valeur).setBackgroundDrawable(
				getActivity().getResources().getDrawable(ressourcesId));
		
		if(afficherFond)
			_couleur.setVisibility(View.VISIBLE);
		else
			_couleur.setVisibility(View.GONE);
			
	}

	/**
	 * Ouvre un timePicker pour changer une heure
	 * Si debut==vrai, alors �a change l'heure de debut, sinon �a change l'heure de fin de la periode
	 * @param debut
	 */
	protected void changerHeure(final boolean debut) {

		Heure h = debut ? periode.getHeureDebut() : periode.getHeureFin();

		TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int heure, int minute) {
						Heure nouvelleHeure = new Heure(heure, minute);
						if (debut)
							periode.setHeureDebut(nouvelleHeure);
						else
							periode.setHeureFin(nouvelleHeure);
						chargerVues();

					}
				}, h.getHeureInt(), h.getMinute(), true);

		timePicker.show();

	}

	/**
	 * Ouvre la popup de selection de couleur, pour la couleur de fond de la periode
	 */
	protected void changerCouleur() {

		if (!active) {
			active = true;
			final afzkl.development.colorpickerview.dialog.ColorPickerDialog colorDialog = new afzkl.development.colorpickerview.dialog.ColorPickerDialog(
					getActivity(), periode.getCouleur());

			colorDialog.setAlphaSliderVisible(false);
			colorDialog.setTitle(getString(R.string.choisir_color));
			colorDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							periode.setCouleur(colorDialog.getColor());
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

	/**
	 * Recupere les donnees, cree la periode, retire le fragment, et appelle le delegate
	 */
	protected void envoyer() {

		String nom = ((TextView) _nom.findViewById(R.id.valeur)).getText()
				.toString();

		if (nom.length() == 0) {
			Toast.makeText(getActivity(), R.string.veuillez_indiquer_un_nom,
					Toast.LENGTH_SHORT).show();
		} else {
			periode.setNom(nom);
			this.delegate.onPeriodeAjoutee(periode);
			super.retirerFragment();
		}
	}

	/**
	 * Appelle le fragment de selection d'indiagram
	 */
	protected void changerImage() {
		ajouterFragment(new SelectionnerIndiagramFragment(this));
	}

	@Override
	public void onIndiagramSelected(Indiagram indiagram) {
		periode.setIndiagramPath(indiagram.filePath);
		periode.setAfficherFondCouleur(this.afficherFond);
		chargerIndiagram();
	}

}
