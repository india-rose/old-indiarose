package org.indiarose.indiarosetimebar.fragments;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.canvas.horloge.Cercle;
import org.indiarose.indiarosetimebar.canvas.horloge.core.Horloge;
import org.indiarose.indiarosetimebar.fragments.core.BarreDeTempsFragmentNormal;
import org.indiarose.indiarosetimebar.model.Modele;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.TextView;

/**
 * Affiche tous les modeles sauvegardes, et retourne le resultat dans le ModeleSelectionFragmentDelegate.onModeleChoisit(Modele modele);
 * @author florentchampigny
 * Utilise un ViewPager
 * Fournit un PagerAdapter pour afficher les horloges dans le ViewPager
 *
 */
@SuppressLint("ValidFragment")
public class ModeleSelectionFragment extends BarreDeTempsFragmentNormal {

	public interface ModeleSelectionFragmentDelegate {
		public void onModeleChoisit(Modele modele);
	}

	List<Modele> modeles = new ArrayList<Modele>();
	ViewPager pager;
	PagerTabStrip strip;

	ModeleSelectionFragmentDelegate delegate;

	View valider;

	public ModeleSelectionFragment(ModeleSelectionFragmentDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState,
				R.layout.selection_modele);
		modeles = getDonnees().getModeles();

		ajouterVues();
		ajouterListener();
		remplirVues();

		return getFragmentView();
	}

	private void ajouterVues() {
		pager = (ViewPager) findViewById(R.id.selection_modele_pager);
		strip = PagerTabStrip.class
				.cast(findViewById(R.id.selection_modele_pager_title_strip));
		valider = findViewById(R.id.valider);
	}

	private void ajouterListener() {
		valider.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.valider) {
			valider();
		}
	}

	private void remplirVues() {
		pager.setAdapter(new HorlogePagesAdapter());

		strip.setDrawFullUnderline(true);
		strip.setTabIndicatorColor(getActivity().getResources().getColor(
				android.R.color.white));
		strip.setBackgroundColor(getActivity().getResources().getColor(
				R.color.couleur_principale_hover));
		strip.setNonPrimaryAlpha(0.5f);
		strip.setTextSpacing(25);
		strip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

	}

	private void valider() {
		if (delegate != null)
			delegate.onModeleChoisit(modeles.get(pager.getCurrentItem()));
		retirerFragment();
	}

	public class HorlogePagesAdapter extends PagerAdapter {
		List<View> layouts = new ArrayList<View>();
		List<Horloge> horloges = new ArrayList<Horloge>();

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View layout = null;

			if (position < layouts.size()) {
				layout = layouts.get(position);
			} else {
				Modele modele = modeles.get(position);

				layout = getLayoutInflater().inflate(
						R.layout.selection_modele_element, null);

				TextView title = (TextView) layout
						.findViewById(android.R.id.title);
				title.setText(modele.getNom());

				Horloge horloge;
				if (position < horloges.size()) {
					horloge = horloges.get(position);
				} else {
					horloge = new Cercle(getActivity(), modele.getPeriodes(),
							false, getIndiagramManager());
					horloges.add(horloge);
				}

				horloge.invalidate();

				horloge.calculerMeilleurTaille();

				if (horloge.getParent() != null)
					((ViewGroup) horloge.getParent()).removeView(horloge);

				ViewGroup layoutHorloge = (ViewGroup) layout
						.findViewById(android.R.id.content);
				layoutHorloge.addView(horloge);

				layouts.add(layout);
			}

			if (layout.getParent() != null)
				((ViewGroup) layout.getParent()).removeView(layout);

			layout.invalidate();

			((ViewPager) container).addView(layout, position);
			return layout;

		}

		@Override
		public int getCount() {
			return modeles.size();
		}

		@SuppressLint("DefaultLocale")
		@Override
		public CharSequence getPageTitle(int position) {
			String nom = modeles.get(position).getNom();
			if (nom.length() > 0) {
				nom = nom.substring(0, 1).toUpperCase() + nom.substring(1);
			}
			return nom;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);
		}
	}

}
