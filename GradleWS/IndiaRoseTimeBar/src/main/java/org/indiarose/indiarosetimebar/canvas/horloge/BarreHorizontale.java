package org.indiarose.indiarosetimebar.canvas.horloge;

import java.util.List;

import org.indiarose.api.IndiagramManager;
import org.indiarose.indiarosetimebar.canvas.horloge.core.Horloge;
import org.indiarose.indiarosetimebar.model.Periode;
import org.indiarose.indiarosetimebar.utils.DisplayConverter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;

@SuppressLint("ViewConstructor")
public class BarreHorizontale extends Horloge {

	@SuppressLint("NewApi")
	public BarreHorizontale(Context context, List<Periode> periodes,
			boolean modifiable, IndiagramManager indiagramManager) {
		super(context, periodes, modifiable, indiagramManager);
	}

	@Override
	public void dessiner() {

		setTailleTraitHeure(getHeight());
		setTailleUneHeure(getWidth() / TAILLE_HEURES);
		setSurfaceDessinable(0, 0, getWidth(), getHeight());

	}

	@Override
	public void calculerMeilleurTaille() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			setTaillePolice(DisplayConverter.spToPixels(getContext(), 15f));
		else
			setTaillePolice(DisplayConverter.spToPixels(getContext(), 13f));

		setDistanceTextes(DisplayConverter.dipToPixels(getContext(), 20f));

	}

	@Override
	public void dessinerPeriode(Periode periode) {
		float[] positionsDebut = nombreToPosition(periode.getHeureDebut()
				.getHeure(), 0);
		float[] positionsFin = nombreToPosition(periode.getHeureFin()
				.getHeure(), 0);

		float distanceIndiagram = 3.0f / 4.0f;
		float tailleIndiagramMax = DisplayConverter.dipToPixels(getContext(),
				50.0f);

		if (periode.getHeureDebut().getHeure() < periode.getHeureFin()
				.getHeure()) {

			RectF rectanglePeriode = new RectF(positionsDebut[0], 0,
					positionsFin[0], getHeight());

			if (periode.isAfficherFondCouleur()) {

				getPaint().setColor(periode.getCouleur());
				getCanvas().drawRect(rectanglePeriode, getPaint());

			}

			getPaint().setColor(Color.BLACK);
			getPaint().setTextSize(getTaillePolice());
			getPaint().setTextAlign(Paint.Align.CENTER);
			getCanvas().drawText(periode.getNom(), rectanglePeriode.centerX(),
					rectanglePeriode.centerY(), getPaint());

			if (periode.chargerIndiagram(getIndiagramManager())) {

				float taille = rectanglePeriode.width() / 2.0f;
				if (taille > tailleIndiagramMax)
					taille = tailleIndiagramMax;

				RectF rectangleImage = new RectF(rectanglePeriode.centerX()
						- taille, rectanglePeriode.height() * distanceIndiagram
						- taille, rectanglePeriode.centerX() + taille,
						rectanglePeriode.height() * distanceIndiagram + taille);

				getCanvas().drawBitmap(periode.getIndiagramBitmap(), null,
						rectangleImage, getPaint());

			}

		} else {
			RectF rectanglePeriode1 = new RectF(positionsDebut[0], 0,
					getWidth(), getHeight());
			RectF rectanglePeriode2 = new RectF(0, 0, positionsFin[0],
					getHeight());

			if (periode.isAfficherFondCouleur()) {

				getPaint().setColor(periode.getCouleur());
				getCanvas().drawRect(rectanglePeriode1, getPaint());

				getPaint().setColor(periode.getCouleur());
				getCanvas().drawRect(rectanglePeriode2, getPaint());

			}
			getPaint().setColor(Color.BLACK);
			getPaint().setTextSize(getTaillePolice());
			getPaint().setTextAlign(Paint.Align.CENTER);
			getCanvas().drawText(periode.getNom(), rectanglePeriode1.centerX(),
					rectanglePeriode1.centerY(), getPaint());
			getCanvas().drawText(periode.getNom(), rectanglePeriode2.centerX(),
					rectanglePeriode2.centerY(), getPaint());

			if (periode.chargerIndiagram(getIndiagramManager())) {

				float taille = rectanglePeriode1.width() / 2.0f;
				if (taille > tailleIndiagramMax)
					taille = tailleIndiagramMax;

				RectF rectangleImage = new RectF(rectanglePeriode1.centerX()
						- taille, rectanglePeriode1.height()
						* distanceIndiagram - taille,
						rectanglePeriode1.centerX() + taille,
						rectanglePeriode1.height() * distanceIndiagram + taille);

				getCanvas().drawBitmap(periode.getIndiagramBitmap(), null,
						rectangleImage, getPaint());

				float taille2 = rectanglePeriode2.width() / 2.0f;
				if (taille2 > tailleIndiagramMax)
					taille2 = tailleIndiagramMax;

				RectF rectangleImage2 = new RectF(rectanglePeriode2.centerX()
						- taille2, rectanglePeriode2.height()
						* distanceIndiagram - taille2,
						rectanglePeriode2.centerX() + taille2,
						rectanglePeriode2.height() * distanceIndiagram
								+ taille2);

				getCanvas().drawBitmap(periode.getIndiagramBitmap(), null,
						rectangleImage2, getPaint());

			}

		}

	}

	public float[] nombreToPosition(float nombre, float distance) {

		float x = getWidth() / TAILLE_HEURES * nombre;
		float y = distance;

		return new float[] { x, y };
	}

	@Override
	public void dessinerTrait(float nombre, float taille) {

		float[] positionDebut = nombreToPosition(nombre, 0);
		float[] positionFin = nombreToPosition(nombre, taille);

		getCanvas().drawLine(positionDebut[0], positionDebut[1],
				positionFin[0], positionFin[1], getPaint());
	}

	@Override
	public float positionToNombre(float x, float y) {
		float nombre = TAILLE_HEURES / getRectangleDessin().width() * x;
		System.out.println(nombre);
		return nombre;
	}

}
