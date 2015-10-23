package org.indiarose.indiarosetimebar.parametres.json;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import org.indiarose.indiarosetimebar.canvas.horloge.TypeHorloge;
import org.indiarose.indiarosetimebar.parametres.Parametres;

import android.content.Context;
import android.os.Environment;

public class JSonParametres extends Parametres {

	public static String fichierJson = "/IndiaRoseChronoGram/parametres.json";
	private File fichier = null;

	private ParametreObjet parametres;

	public JSonParametres(Context context) {
		super(context);
		try {
			fichier = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + fichierJson);
			fichier.createNewFile();

			parametres = chargerParametres();

		} catch (Exception e) {
			fichier = null;
			e.printStackTrace();
		}
	}

	protected ParametreObjet chargerParametres() {
		ParametreObjet parametres = null;
		if (fichier != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				parametres = mapper.readValue(fichier, ParametreObjet.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return parametres;
	}

	protected void sauvegarder(ParametreObjet parametres) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(fichier, parametres);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sauvegarder() {
		this.sauvegarder(this.parametres);
	}

	@Override
	public TypeHorloge getType() {
		return parametres.getType();
	}

	@Override
	public void setType(TypeHorloge type) {
		parametres.setType(type);
	}

	@Override
	public boolean isAfficherFond() {
		return parametres.isAfficherFond();
	}

	@Override
	public void setAfficherFond(boolean afficherFond) {
		parametres.setAfficherFond(afficherFond);
	}

	@Override
	public boolean isAfficherJours() {
		return parametres.isAfficherJours();
	}

	@Override
	public void setAfficherJours(boolean afficherJours) {
		parametres.setAfficherJours(afficherJours);
	}

	@Override
	public int getCouleurAujourdhui() {
		return parametres.getCouleurAujourdhui();
	}

	@Override
	public void setCouleurAujourdhui(int couleurAujourdhui) {
		parametres.setCouleurAujourdhui(couleurAujourdhui);
	}

	@Override
	public int getCouleurJours() {
		return parametres.getCouleurJours();
	}

	@Override
	public void setCouleurJours(int couleurJours) {
		parametres.setCouleurJours(couleurJours);
	}

	@Override
	public int getIntervalleCalendrierMois() {
		return parametres.getNbMois();
	}

	@Override
	public void setIntervalleCalendrierMois(int nbMois) {
		parametres.setNbMois(nbMois);
	}

	@Override
	public int getCouleurJoursAvecBarreDeTemps() {
		return parametres.getCouleurJoursAvecBarreDeTemps();
	}

	@Override
	public void setCouleurJoursAvecBarreDeTemps(int couleurJoursAvecBarreDeTemps) {
		parametres
				.setCouleurJoursAvecBarreDeTemps(couleurJoursAvecBarreDeTemps);
	}

	@Override
	public boolean isDessinerFondHeureDynamique() {
		return parametres.isDessinerFondHeureDynamique();
	}

	@Override
	public void setDessinerFondHeureDynamique(boolean dessinerFondHeureDynamique) {
		parametres.setDessinerFondHeureDynamique(dessinerFondHeureDynamique);
	}

	@Override
	public void setOrientation(int orientation) {
		parametres.setOrientation(orientation);
	}

	@Override
	public int getOrientation() {
		return parametres.getOrientation();
	}

}
