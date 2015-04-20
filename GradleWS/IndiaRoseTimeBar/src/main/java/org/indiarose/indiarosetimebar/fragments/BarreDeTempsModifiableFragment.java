package org.indiarose.indiarosetimebar.fragments;

import java.util.List;

import org.indiarose.api.fragments.SelectionnerIndiagramFragment;
import org.indiarose.api.fragments.SelectionnerIndiagramFragment.SelectionnerIndiagramDelegate;
import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.adapter.SelectionListAdapter;
import org.indiarose.indiarosetimebar.canvas.agenda.Agenda;
import org.indiarose.indiarosetimebar.canvas.horloge.core.Horloge;
import org.indiarose.indiarosetimebar.fragments.ModeleSelectionFragment.ModeleSelectionFragmentDelegate;
import org.indiarose.indiarosetimebar.model.*;
import org.indiarose.indiarosetimebar.views.core.ListElementView.ListElementViewClickDelegate;
import org.indiarose.lib.model.Indiagram;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Fragment permettant d'afficher un ChronoGram
 * Ce dernier etend BarreDeTemps et la rend modifiable
 * 
 * voir BarreDeTempsFragment
 * 
 * @author florentchampigny
 *
 */

@SuppressLint("ValidFragment")
public class BarreDeTempsModifiableFragment extends BarreDeTempsFragment
implements Horloge.PeriodeChoosenDelegate,
AjouterPeriodeFragment.AjouterPeriodeFragmentDelegate,
Agenda.AgendaDelegate, SelectionnerIndiagramDelegate,
ListElementViewClickDelegate, ModeleSelectionFragmentDelegate {

	View _layoutHautModifier;
	View _sauvegarder;
	View _chargerModele;
	View _afficherListe;
	ViewGroup _layoutListe;
	static boolean listeVisible = false;
	static ListView liste = null;
	static SelectionListAdapter<Periode> adapter;
	View _supprimerElements;

	static boolean modificationRecente = false;

	public BarreDeTempsModifiableFragment() {
	}
	
	public BarreDeTempsModifiableFragment(List<Jour> jours,
			List<Periode> periodes) {
		BarreDeTempsFragment.modifiable = true;
		BarreDeTempsFragment.jours = jours;
		BarreDeTempsFragment.periodes = periodes;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if (id == R.id.barre_temps_affichage_liste) {
			switchAffichageListe();
		} else if (id == R.id.barre_temps_supprimer_elements) {
			supprimerElementsSelectionnes();
		} else if (id == R.id.barre_temps_sauvegarder) {
			sauvegarder();
		} else if (id == R.id.barre_temps_charger) {
			chargerModele();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		affichageListe();
	}

	protected void ajouterVues() {
		super.ajouterVues();
		_layoutHautModifier = findViewById(R.id.barre_temps_layout_haut_modifier);
		_afficherListe = findViewById(R.id.barre_temps_affichage_liste);
		_layoutListe = (ViewGroup) findViewById(R.id.barre_temps_layout_liste);
		_supprimerElements = findViewById(R.id.barre_temps_supprimer_elements);
		_sauvegarder = findViewById(R.id.barre_temps_sauvegarder);
		_chargerModele = findViewById(R.id.barre_temps_charger);
	}

	protected void chargerVues() {
		super.chargerVues();
		afficherHorloge();

		_layoutHautModifier.setVisibility(View.VISIBLE);
		_layoutListe.setVisibility(View.GONE);
		_supprimerElements.setVisibility(View.GONE);

		if (getDonnees().getModeles().size() == 0)
			_chargerModele.setVisibility(View.GONE);

	}

	protected void ajouterListener() {
		horloge.setPeriodeChoosenDelegate(this);
		_afficherListe.setOnClickListener(this);
		_supprimerElements.setOnClickListener(this);
		_layoutListe.setOnClickListener(this);
		_sauvegarder.setOnClickListener(this);
		_chargerModele.setOnClickListener(this);
	}

	/**
	* Cette fois-ci l'agenda est modifiable
	*/
	@Override
	protected void creerAgenda() {
		agenda = new Agenda(getActivity(), semaine, true, this,
				getIndiagramManager());
	}

	public boolean onBackPressed() {
		if(listeVisible){
			switchAffichageListe();
			return true;
		}else if(modificationRecente){
			sauvegarder(true);
			return true;
		}
		return false;
	}

	/**
	 * Change l'affichage des periode, passe en liste ou en ChronoGram
	 */
	protected void switchAffichageListe() {
		listeVisible = !listeVisible;
		affichageListe();
	}
	
	/**
	 * Permet d'afficher une listView afin de gerer les periodes affichees
	 */
	protected void affichageListe(){
		if (listeVisible) {
			((Button) _afficherListe).setText(getActivity().getResources()
					.getString(R.string.afficher_chronogram));
			_layoutListe.setVisibility(View.VISIBLE);
			if (liste == null && adapter == null) {
				liste = (ListView) getLayoutInflater().inflate(
						R.layout.listview_periodes, null);
				adapter = new SelectionListAdapter<Periode>(getActivity(),
						periodes, this, true);
			} else
				adapter.notifyDataSetChanged();
			
			try{
				((ViewGroup)liste.getParent()).removeView(liste);
			}catch(Exception e){
				
			}
			_layoutListe.addView(liste);
			liste.setAdapter(adapter);
		} else {
			((Button) _afficherListe).setText(getActivity().getResources()
					.getString(R.string.afficher_liste));
			_layoutListe.setVisibility(View.GONE);
			horloge.reDraw();
		}
	}

	/**
	 * Permet de supprimer les elements selectionnes
	 * Demande a l'utilisateur la confirmation de suppression
	 */
	protected void supprimerElementsSelectionnes() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getActivity().getString(R.string.supprimer_les_elements)
				+ " ?");
		alert.setPositiveButton(getActivity().getString(R.string.oui),
				new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				periodes.removeAll(adapter.getSelectedObjects());
				adapter.notifyDataSetChanged();
				modificationRecente = true;
				_supprimerElements.setVisibility(View.GONE);
				dialog.dismiss();
			}
		});
		alert.setNegativeButton(getActivity().getString(R.string.non),
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onListElementViewLongClicked(null);
				dialog.dismiss();
			}

		});

		alert.show();

	}


	
	@Override
	public void onPeriodeChoosen(float heure) {
		ajouterFragment(new AjouterPeriodeFragment(this, heure));
	}

	@Override
	public void onPeriodeAjoutee(Periode periode) {
		if(!BarreDeTempsFragment.periodes.contains(periode))
			BarreDeTempsFragment.periodes.add(periode);

		modificationRecente = true;
		//sauvegarderPeriodesDansJours();

		BarreDeTempsFragment.horloge.setPeriodeTemporaire(null);
		BarreDeTempsFragment.horloge.reDraw();
	}

	/**
	 * Sauvegarde sans quitter le fragment
	 */
	protected void sauvegarder() {
		sauvegarder(false);
	}
	/**
	 * Sauvegarde les periodes crees / modifiees dans :
	 * 1)  les jours fournits en parametre de creation
	 * 2) En modele de jours
	 * Demande a l'utilisateur ou le sauvegarder
	 * @param quitter
	 */
	protected void sauvegarder(final boolean quitter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(getActivity().getResources().getString(
				R.string.sauvegarder));
		String[] array = new String[] {
				getActivity().getResources().getString(
						R.string.en_tant_que_modele_de_journee),
						getActivity().getResources().getString(
								R.string.dans_mes_chronograms) };

		builder.setItems(array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					demanderNomModele();
					break;
				case 1:
					sauvegarderPeriodesDansJours();
					modificationRecente = false;
					if(quitter)
						retirerFragment();
					break;
				}
			}
		});

		builder.setPositiveButton(getActivity().getResources().getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});

		builder.setNegativeButton(getActivity().getResources().getString(R.string.quitter), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				retirerFragment();
			}
		});

		builder.create().show();
	}

	protected void chargerModele() {
		ajouterFragment(new ModeleSelectionFragment(this));
	}

	/**
	 * Demande le nom du modele lors de la creation
	 */
	protected void demanderNomModele() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle(getActivity().getString(R.string.nom_du_modele));

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setHint(R.string.nom_du_modele);
		alert.setView(input);

		alert.setPositiveButton(getActivity().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				sauvegarderModele(value);
				dialog.dismiss();
			}
		});

		alert.setNegativeButton(getActivity().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();
	}

	/**
	 * Sauvegarde le modele avec le nom fournit en parametre
	 * @param nom nom du modele
	 */
	protected void sauvegarderModele(String nom) {

		Modele m = new Modele(nom, periodes);
		getDonnees().getModeles().add(m);
		getPersistance().sauvegarder(getDonnees());
		Toast.makeText(getActivity(), R.string.modele_sauvegarde,
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Sauvegarde les periodes crees/modifiees dans les jours passees en parametre lors de la creation
	 */
	protected void sauvegarderPeriodesDansJours() {
		for (Jour jour : jours)
			jour.setPeriodes(BarreDeTempsFragment.periodes);
		getPersistance().sauvegarder(getDonnees());
		Toast.makeText(getActivity(), R.string.chronogram_sauvegardee,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onJourClicked(final Jour jour) {
		jourTmp = jour;

		if (jour.getIndiagramPath() != null) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setPositiveButton(getActivity()
					.getString(R.string.modifier), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					ajouterFragment(new SelectionnerIndiagramFragment(
							BarreDeTempsModifiableFragment.this));
				}
			});

			builder.setNegativeButton(
					getActivity().getString(R.string.retirer),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							onIndiagramSelected(null);
							dialog.dismiss();
							agenda.reDraw();
						}
					});

			builder.create().show();
		} else
			ajouterFragment(new SelectionnerIndiagramFragment(this));
	}

	@Override
	public void onIndiagramSelected(Indiagram indiagram) {
		if (indiagram != null)
			jourTmp.setIndiagramPath(indiagram.filePath);
		else
			jourTmp.setIndiagramPath(null);
		horloge.reDraw();
	}

	@Override
	public void onListElementViewClicked(Object objet) {
		if (objet instanceof Periode) {
			ajouterFragment(new AjouterPeriodeFragment(this, (Periode) objet));
		} else { // sinon, c'est un ajout
			ajouterFragment(new AjouterPeriodeFragment(this));
		}
	}

	@Override
	public void onListElementViewLongClicked(Object objet) {
		if (adapter.getSelectedViews().isEmpty()) {
			_supprimerElements.setVisibility(View.GONE);
		} else
			_supprimerElements.setVisibility(View.VISIBLE);

	}

	@Override
	public void onModeleChoisit(Modele modele) {
		periodes.addAll(modele.getPeriodes());
	}


}
