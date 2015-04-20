package org.indiarose.indiarosetimebar.parametres.json;

import org.indiarose.indiarosetimebar.canvas.horloge.TypeHorloge;

import android.content.pm.ActivityInfo;
import android.graphics.Color;

public class ParametreObjet {

	private int nbMois = 1;
	private TypeHorloge type = TypeHorloge.CERCLE;
	private boolean afficherFond = true;
	private boolean afficherJours = true;
	private boolean dessinerFondHeureDynamique = false;
	private int couleurAujourdhui = Color.YELLOW;
	private int couleurJours = Color.BLACK;
	private int couleurJoursAvecBarreDeTemps = Color.parseColor("#fca3ee");
	private int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

	public ParametreObjet() {
	}

	public int getNbMois() {
		return nbMois;
	}

	public void setNbMois(int nbMois) {
		this.nbMois = nbMois;
	}

	public TypeHorloge getType() {
		return type;
	}

	public void setType(TypeHorloge type) {
		this.type = type;
	}

	public boolean isAfficherFond() {
		return afficherFond;
	}

	public void setAfficherFond(boolean afficherFond) {
		this.afficherFond = afficherFond;
	}

	public boolean isAfficherJours() {
		return afficherJours;
	}

	public void setAfficherJours(boolean afficherJours) {
		this.afficherJours = afficherJours;
	}

	public int getCouleurAujourdhui() {
		return couleurAujourdhui;
	}

	public void setCouleurAujourdhui(int couleurAujourdhui) {
		this.couleurAujourdhui = couleurAujourdhui;
	}

	public int getCouleurJours() {
		return couleurJours;
	}

	public void setCouleurJours(int couleurJours) {
		this.couleurJours = couleurJours;
	}

	public int getCouleurJoursAvecBarreDeTemps() {
		return couleurJoursAvecBarreDeTemps;
	}

	public void setCouleurJoursAvecBarreDeTemps(int couleurJoursAvecBarreDeTemps) {
		this.couleurJoursAvecBarreDeTemps = couleurJoursAvecBarreDeTemps;
	}

	public boolean isDessinerFondHeureDynamique() {
		return dessinerFondHeureDynamique;
	}

	public void setDessinerFondHeureDynamique(boolean dessinerFondHeureDynamique) {
		this.dessinerFondHeureDynamique = dessinerFondHeureDynamique;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

}