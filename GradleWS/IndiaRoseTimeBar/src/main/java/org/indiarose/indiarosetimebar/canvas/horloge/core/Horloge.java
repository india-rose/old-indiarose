package org.indiarose.indiarosetimebar.canvas.horloge.core;

import java.util.*;

import org.indiarose.api.IndiagramManager;
import org.indiarose.indiarosetimebar.canvas.core.ZoneDessin;
import org.indiarose.indiarosetimebar.factory.PeriodeFactory;
import org.indiarose.indiarosetimebar.model.Periode;
import org.indiarose.indiarosetimebar.parametres.Parametres;
import org.indiarose.indiarosetimebar.utils.DateManager;
import org.indiarose.indiarosetimebar.utils.DisplayConverter;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;

/**
 * Zone de dessin de l'horloge
 * Classe abstraite a implementer pour chaque forme de dessin (cercle, horizontal, ...)
 * @author florentchampigny
 */
public abstract class Horloge extends ZoneDessin {

	/**
	 * Est appelle lors de la selection d'une periode
	 * @author florentchampigny
	 */
	public interface PeriodeChoosenDelegate {
		public void onPeriodeChoosen(float heure);
	}

	public static final int TAILLE_HEURES = 24; //Nombre d'heures a afficher
	float tailleTraitGrand = 20;
	float tailleTraitPetit = 10;
	float taillePolice = 20;

	float distanceTextes = 0;
	float tailleUneHeure = 1;

	List<Periode> periodesFond = PeriodeFactory.genererPeriodesFond(); //Periodes de fond de l'horloge (matin, soir, etc)
	List<Periode> periodesJour = null; //periodes actuelles affichees
	Periode periodeTemporaire = null; //periode temporaire (lors du click)

	float tailleTraitHeure = 50;

	PeriodeChoosenDelegate periodeChoosenDelegate = null;

	Parametres parametres;

	IndiagramManager indiagramManager = null;

	public Horloge(Context context, List<Periode> periodes, boolean modifiable,
			IndiagramManager indiagramManager) {
		super(context, modifiable);

		this.parametres = Parametres.getInstance(getContext());

		this.periodesJour = periodes;
		this.indiagramManager = indiagramManager;

		lancerAsynckTaskDessin();
	}

	public IndiagramManager getIndiagramManager() {
		return indiagramManager;
	}

	/**
	 * Lance une tache asynchrone qui force le dessin toutes les 5 minutes
	 */
	private void lancerAsynckTaskDessin() {

		int tempsRaffraichissementHeureMinutes = 5;
		int tempsRaffraichissementHeureSecondes = tempsRaffraichissementHeureMinutes * 60;
		int tempsRaffraichissementHeureMilliSecondes = tempsRaffraichissementHeureSecondes * 1000;

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				new DessinerHeureAsynckTask().execute();
			}
		}, 0, tempsRaffraichissementHeureMilliSecondes);
	}

	public void dessinerPeriodes() {
		if (parametres.isAfficherFond())
			for (Periode periode : periodesFond) {
				dessinerPeriode(periode);
			}
		for (Periode periode : periodesJour) {
			dessinerPeriode(periode);
			System.out.println(periode);
		}
		if (periodeTemporaire != null)
			dessinerPeriode(periodeTemporaire);
	}

	public void click(float x, float y) {
		final float heure = arrondir(positionToNombre(x, y));
		if (periodeTemporaire != null && periodeTemporaire.contains(heure)
				&& periodeChoosenDelegate != null) {
			((Activity) getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					periodeChoosenDelegate.onPeriodeChoosen(heure);
				}

			});
		} else
			setPeriodeTemporaire(heure);
	}

	public void setPeriodeTemporaire(float heure) {
		periodeTemporaire = new Periode("+", Color.parseColor("#8833b5e5"),
				heure - 1f, heure + 1f);
		reDraw();
	}

	/**
	 * A implementer, gere le dessin d'une periode sur l'horloge
	 */
	public abstract void dessinerPeriode(Periode periode);

	public void dessinerHeure() {
		getPaint().setStrokeWidth(10);
		float heure = DateManager.getHeure();

		float[] positionDebut = nombreToPosition(heure, 0);
		float[] positionFin = nombreToPosition(heure, getTailleTraitHeure());

		getCanvas().drawLine(positionDebut[0], positionDebut[1],
				positionFin[0], positionFin[1], getPaint());

		getPaint().setStrokeWidth(2);
	}

	/**
	 * A implementer, converti une position (x,y) en nombre, en fonction du dessin
	 */
	public abstract float positionToNombre(float x, float y);

	/**
	 * A implementer, converti un nombre en position (x,y), en fonction du dessin
	 */
	public abstract float[] nombreToPosition(float nombre, float distance);

	/**
	 * Arrondit un nombre au plus pres (0, 0.5, 1)
	 * Si n = 1.20 => retourne 1
	 * Si n = 4.35 => retourne 4.5
	 * Si n = 2.82 => retourne 3
	 * @param nombre
	 * @return
	 */
	public float arrondir(float nombre) {
		int entiere = (int) nombre;
		float reste = nombre - entiere;
		if (reste < 0.25f)
			return entiere;
		else if (reste > 0.75f)
			return entiere + 1;
		else
			return entiere + 0.5f;
	}

	public void clear(Canvas canvas) {
		if (canvas != null)
			canvas.drawColor(Color.WHITE);
	}

	/**
	 * A implementer, gere le dessin de l'horloge
	 */
	public abstract void dessiner();

	public void dessinerTextes() {

		getPaint().setColor(Color.BLACK);
		getPaint().setTextSize(taillePolice);
		getPaint().setTextAlign(Paint.Align.CENTER);

		for (int i = 0; i < TAILLE_HEURES; i++) {

			String texte = Integer.valueOf(i).toString();
			if (i == 0)
				texte = "24";

			float[] position = nombreToPosition(i, distanceTextes);

			getCanvas().drawText(texte, position[0], position[1], getPaint());

			dessinerTrait(i * tailleUneHeure,
					DisplayConverter.dipToPixels(getContext(), 10f));
		}
	}

	public abstract void dessinerTrait(float nombre, float taille);

	public void dessinerTraits() {
		for (int i = 0; i < TAILLE_HEURES; i++)
			dessinerTrait(i, tailleTraitGrand);
		for (float i = 0.5f; i < TAILLE_HEURES; i += 1)
			dessinerTrait(i, tailleTraitPetit);
	}

	public void dessinerContour() {
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (canvas != null) {
			if (parametres.isDessinerFondHeureDynamique())
				dessinerFondHeure(canvas);
			dessiner();

			dessinerPeriodes();
			dessinerContour();
			dessinerTextes();
			dessinerTraits();
			dessinerHeure();

			// canvas.scale(0.5f, 0.5f);
		}

	}

	/**
	 * Change la couleur du fond (blanc vers noir) en fonction de l'heure
	 */
	private void dessinerFondHeure(Canvas canvas) {
		double heureActuelle = DateManager.getHeure();

		double demiJournee = (TAILLE_HEURES / 2);

		double distance = heureActuelle < demiJournee ? demiJournee
				- heureActuelle : heureActuelle - demiJournee;

		float pourcentage = (float) (distance / demiJournee);

		float couleurMax = 255.0f;
		float couleurMin = 50.0f;

		float couleurFloat = couleurMax
				- ((couleurMax - couleurMin) * pourcentage);

		int couleur = (int) couleurFloat;

		try {
			canvas.drawARGB(255, couleur, couleur, couleur);
		} catch (Exception e) {
		}
	}

	// -----------------------------GETTERS AND
	// SETTERS----------------------------

	public List<Periode> getPeriodesFond() {
		return periodesFond;
	}

	public void setPeriodesFond(List<Periode> periodesFond) {
		this.periodesFond = periodesFond;
	}

	public void addPeriodesFond(Periode... ps) {
		for (Periode periode : ps)
			this.periodesFond.add(periode);
	}

	public Periode getPeriodeTemporaire() {
		return periodeTemporaire;
	}

	public float getDistanceTextes() {
		return distanceTextes;
	}

	public void setDistanceTextes(float distanceTextes) {
		this.distanceTextes = distanceTextes;
	}

	public float getTailleTraitGrand() {
		return tailleTraitGrand;
	}

	public void setTailleTraitGrand(float tailleTraitGrand) {
		this.tailleTraitGrand = tailleTraitGrand;
	}

	public List<Periode> getPeriodesJour() {
		return periodesJour;
	}

	public void setPeriodesJour(List<Periode> periodesJour) {
		this.periodesJour = periodesJour;
	}

	public float getTailleTraitPetit() {
		return tailleTraitPetit;
	}

	public void setTailleTraitPetit(float tailleTraitPetit) {
		this.tailleTraitPetit = tailleTraitPetit;
	}

	public float getTailleUneHeure() {
		return tailleUneHeure;
	}

	public void setTailleUneHeure(float tailleUneHeure) {
		this.tailleUneHeure = tailleUneHeure;
	}

	public float getTaillePolice() {
		return taillePolice;
	}

	public void setTaillePolice(float taillePolice) {
		this.taillePolice = taillePolice;
	}

	public void setPeriodeTemporaire(Periode periodeTemporaire) {
		this.periodeTemporaire = periodeTemporaire;
	}

	public PeriodeChoosenDelegate getPeriodeChoosenDelegate() {
		return periodeChoosenDelegate;
	}

	public void setPeriodeChoosenDelegate(
			PeriodeChoosenDelegate periodeChoosenDelegate) {
		this.periodeChoosenDelegate = periodeChoosenDelegate;
	}

	/*------------------ASYNCK TASK DESSIN HEURE --------------------------- */

	public float getTailleTraitHeure() {
		return tailleTraitHeure;
	}

	public void setTailleTraitHeure(float tailleTraitHeure) {
		this.tailleTraitHeure = tailleTraitHeure;
	}

	private class DessinerHeureAsynckTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			((Activity) Horloge.this.getContext())
					.runOnUiThread(new Runnable() {
						public void run() {
							Horloge.this.invalidate();
							System.out.println("dessin");
						}
					});

			return null;
		}
	}

}
