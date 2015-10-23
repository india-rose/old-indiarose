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
import android.view.Display;
import android.view.WindowManager;

@SuppressLint("ViewConstructor")
public class Cercle extends Horloge {

	private int rayon;

	private static float angleAjoute = 70;

	public Cercle(Context context, List<Periode> periodes, boolean modifiable,
			IndiagramManager indiagramManager) {
		super(context, periodes, modifiable, indiagramManager);

		setTailleUneHeure(360 / TAILLE_HEURES);

	}

	@Override
	public void dessiner() {

		setSurfaceDessinable(getWidth() / 2 - rayon, getHeight() / 2 - rayon,
				getWidth() / 2 + rayon, getHeight() / 2 + rayon);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void calculerMeilleurTaille() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			this.rayon = display.getWidth() / 2
					- (int) DisplayConverter.dipToPixels(getContext(), 40f);
			setTaillePolice(DisplayConverter.spToPixels(getContext(), 14f));
		} else {
			this.rayon = display.getHeight() / 2
					- (int) DisplayConverter.dipToPixels(getContext(), 100f);
			setTaillePolice(DisplayConverter.spToPixels(getContext(), 10f));
		}

		setTailleTraitHeure(rayon * 3 / 4);
		setDistanceTextes(rayon
				+ DisplayConverter.spToPixels(getContext(), 15f));
	}

	@Override
	public void dessinerPeriode(Periode periode) {

		float milieu;
		if (periode.getHeureDebut().getHeure() < periode.getHeureFin()
				.getHeure())
			milieu = (periode.getHeureDebut().getHeure() + periode
					.getHeureFin().getHeure()) / 2;
		else
			milieu = (periode.getHeureDebut().getHeure()
					+ periode.getHeureFin().getHeure() - TAILLE_HEURES) / 2;

		if (periode.isAfficherFondCouleur()) {
			getPaint().setColor(periode.getCouleur());
			dessinerArc(periode.getHeureDebut().getHeure(), periode
					.getHeureFin().getHeure());
		}

		float[] positions = nombreToPosition(milieu, rayon / 2);

		getPaint().setColor(Color.BLACK);
		getPaint().setTextSize(getTaillePolice());
		getPaint().setTextAlign(Paint.Align.CENTER);
		getCanvas().drawText(periode.getNom(), positions[0], positions[1],
				getPaint());

		if (periode.chargerIndiagram(getIndiagramManager())) {

			float ratio = 4.0f / 5.0f;
			float tailleIndiagram = getWidth() / 18;

			float[] positions2 = nombreToPosition(milieu, rayon * ratio);

			RectF rectangleImage = new RectF(positions2[0] - tailleIndiagram,
					positions2[1] - tailleIndiagram, positions2[0]
							+ tailleIndiagram, positions2[1] + tailleIndiagram);

			getCanvas().drawBitmap(periode.getIndiagramBitmap(), null,
					rectangleImage, getPaint());

		}

	}

	@Override
	public void dessinerContour() {

		getPaint().setStyle(Paint.Style.STROKE);
		getPaint().setColor(Color.BLACK);
		getCanvas().drawCircle(getWidth() / 2, getHeight() / 2, rayon,
				getPaint());

		getPaint().setStyle(Paint.Style.FILL);

	}

	private void dessinerArc(float heureDebut, float heureFin) {
		float[] angles = nombresToAngle(heureDebut, heureFin);

		getCanvas().drawArc(getRectangleDessin(), angles[0], angles[1], true,
				getPaint());
	}

	public float[] nombreToPosition(float nombre, float distance) {
		float x = getWidth()
				/ 2
				+ (float) Math.cos(Math.toRadians(nombre
						* (360 / TAILLE_HEURES) + angleAjoute)) * distance;
		float y = getHeight()
				/ 2
				+ (float) Math.sin(Math.toRadians(nombre
						* (360 / TAILLE_HEURES) + angleAjoute)) * distance;

		return new float[] { x, y };
	}

	public float positionToNombre(float x, float y) {
		float angle = positionToAngle(x, y);
		System.out.println("angle :" + angle);

		float nombre = angleToNombre(angle);

		return arrondir(nombre);
	}

	private float positionToAngle(float x, float y) {
		float largeur = x - getWidth() / 2;
		float hauteur = y - getHeight() / 2;

		return (float) ((Math.toDegrees(Math.atan2(hauteur, largeur))
				- nombreToAngle(0) + 360) % 360);
	}

	private float angleToNombre(float angle) {
		return angle / 360 * TAILLE_HEURES;
	}

	private float nombreToAngle(float nombre) {
		return 360 * nombre / TAILLE_HEURES + angleAjoute;
	}

	private float[] nombresToAngle(float debut, float fin) {

		float angleDebut = nombreToAngle(debut);
		float angleFin;

		if (debut < fin)
			angleFin = 360 * (fin - debut) / TAILLE_HEURES;
		else
			angleFin = 360 * ((TAILLE_HEURES - debut) + fin) / TAILLE_HEURES;

		return new float[] { angleDebut, angleFin };
	}

	@Override
	public void dessinerTrait(float nombre, float taille) {

		float[] positionDebut = nombreToPosition(nombre, rayon - taille);
		float[] positionFin = nombreToPosition(nombre, rayon);

		getCanvas().drawLine(positionDebut[0], positionDebut[1],
				positionFin[0], positionFin[1], getPaint());
	}

	public boolean appartient(float x, float y) {
		float centre_x = x - getWidth() / 2;
		float centre_y = y - getHeight() / 2;

		if (Math.sqrt((centre_x * centre_x) + (centre_y * centre_y)) <= rayon) {
			return true; // sqrt(x²+y²) <= rayon, alors x,y est dans le cercle
		} else {
			return false;
		}
	}

}
