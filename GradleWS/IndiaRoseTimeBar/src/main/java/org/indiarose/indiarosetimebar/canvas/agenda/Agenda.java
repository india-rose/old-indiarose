package org.indiarose.indiarosetimebar.canvas.agenda;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.api.IndiagramManager;
import org.indiarose.indiarosetimebar.R;
import org.indiarose.indiarosetimebar.canvas.core.ZoneDessin;
import org.indiarose.indiarosetimebar.model.Jour;
import org.indiarose.indiarosetimebar.model.Semaine;
import org.indiarose.indiarosetimebar.parametres.Parametres;
import org.indiarose.indiarosetimebar.utils.DateManager;
import org.indiarose.indiarosetimebar.utils.DisplayConverter;
import org.indiarose.lib.model.Indiagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;

/**
 * Classe qui s'occupe de l'affichage des jours de la semaine en bas du chronogram
 * @author florentchampigny
 */
@SuppressLint({ "ViewConstructor", "DefaultLocale" })
public class Agenda extends ZoneDessin {

	Semaine semaine; //La semain a afficher
	float rayon; //le rayon des cercles de jours

	Parametres parametres;
	IndiagramManager indiagramManager;

	List<ZoneJour> zoneJours = new ArrayList<ZoneJour>(); //Les affichages des jours
	AgendaDelegate delegate;
	
	float decalageBas = 20.0f; //la marge entre le cercle et le bas

	/**
	 * Delegate remontant l'information d'un jour selectionne
	 * @author florentchampigny
	 */
	public interface AgendaDelegate {
		public void onJourClicked(Jour jour);
	}

	public Agenda(Context context, Semaine semaine, boolean modifiable,
			AgendaDelegate delegate, IndiagramManager indiagramManager) {
		super(context, modifiable);

		this.parametres = Parametres.getInstance(getContext());
		this.indiagramManager = indiagramManager;

		this.semaine = semaine;
		this.delegate = delegate;
	}

	public Agenda(Context context, Semaine semaine,
			IndiagramManager indiagramManager) {
		this(context, semaine, false, null, indiagramManager);
	}

	@Override
	public void calculerMeilleurTaille() {
	}

	public void dessinerSemaine() {

		if (zoneJours.isEmpty()) {
			int nombreJours = semaine.getJours().size();

			float largeurJour = getWidth() / nombreJours;

			for (int i = 0; i < semaine.getJours().size(); ++i) {
				Jour jour = semaine.getJours().get(i);

				float left = i * largeurJour;
				float right = (i + 1) * largeurJour - 1;
				float bottom = getRectangleDessin().bottom;
				float top = getRectangleDessin().top;

				ZoneJour zone = new ZoneJour(jour, new RectF(left, top, right,
						bottom));

				zoneJours.add(zone);
			}
		} else {
			for (ZoneJour zoneJour : zoneJours) {
				dessinerJour(zoneJour);
			}
		}
	}

	public void dessinerJour(ZoneJour zoneJour) {

		// DESSIN DU TEXTE
		getPaint().setColor(Color.BLACK);
		getPaint().setTextSize(DisplayConverter.spToPixels(getContext(), 12f));
		getPaint().setTextAlign(Paint.Align.CENTER);

		float x = zoneJour.getRectangle().centerX();
		float y = zoneJour.getRectangle().bottom - 20;

		String j = zoneJour.getJour().getJour();
		j = String.valueOf(j.charAt(0)).toUpperCase()
				+ j.subSequence(1, j.length());
		getCanvas().drawText(j, x, y, getPaint());

		if (zoneJour.getJour().getIndiagramPath() == null) {
			// DESSIN DU CERCLE
			getPaint().setStyle(Paint.Style.FILL);

			if (zoneJour.getJour().equals(DateManager.getAujourdhui()))
				getPaint().setColor(parametres.getCouleurAujourdhui());
			else
				getPaint().setColor(parametres.getCouleurJours());

			getCanvas().drawCircle(zoneJour.getRectangle().centerX(),
					zoneJour.getRectangle().centerY() - decalageBas , rayon, getPaint());
		}

		dessinerImage(zoneJour);
	}

	public void dessinerImage(ZoneJour zoneJour) {
		if (zoneJour.getJour().getIndiagramPath() != null) {
			Indiagram indiagram = indiagramManager.getIndiagramByPath(zoneJour
					.getJour().getIndiagramPath());

			float taille = zoneJour.getRectangle().height() / 4;

			float left = zoneJour.getRectangle().centerX() - taille;
			float top = zoneJour.getRectangle().centerY() - taille - decalageBas;
			float bottom = zoneJour.getRectangle().centerY() + taille - decalageBas;
			float right = zoneJour.getRectangle().centerX() + taille;

			if (bottom - top > right - left)
				bottom = top + right - left;

			RectF rectangleImage = new RectF(left, top, right, bottom);

			getCanvas().drawBitmap(indiagram.getImageAsBitmap(), null,
					rectangleImage, getPaint());
		}
	}

	@Override
	public void click(float x, float y) {
		super.click(x, y);

		System.out.println("click");

		for (ZoneJour zoneJour : zoneJours) {
			if (zoneJour.contient(x, y)) {
				delegate.onJourClicked(zoneJour.getJour());
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		this.rayon = getHeight() * 1.0f / 5.0f;

		setSurfaceDessinable(0, 0, getWidth(), getHeight());

		dessinerSemaine();
	}

	public void clear(Canvas canvas) {
		if (canvas != null)
			canvas.drawColor(getContext().getResources().getColor(
					R.color.couleur_semaine));
	}

	@Override
	public boolean appartient(float x, float y) {
		return true;
	}

}
