package org.indiarose.indiarosetimebar.parametres.session;

import org.indiarose.indiarosetimebar.canvas.horloge.TypeHorloge;
import org.indiarose.indiarosetimebar.parametres.Parametres;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

public class SessionParametres extends Parametres {

	protected String PREFERENCES = "PARAMETRES_PREFERENCES";
	protected String KEY = "PARAMETRES";

	protected String TYPE = "TYPE";
	protected String AFFICHER_FOND = "AFFICHER_FOND";
	protected String AFFICHER_JOURS = "AFFICHER_JOURS";
	protected String COULEUR_AUJOURDHUI = "COULEUR_AUJOURDHUI";
	protected String COULEUR_JOURS = "COULEUR_JOURS";
	protected String DESSINER_FOND_HEURE_DYNAMIQUE = "DESSINER_FOND_HEURE_DYNAMIQUE";
	protected String NOMBRE_MOIS = "NOMBRE_MOIS";
	protected String COULEUR_JOURS_AVEC_BARRE_DE_TEMPS = "COULEUR_JOURS_AVEC_BARRE_DE_TEMPS";
	protected String ORIENTATION = "ORIENTATION";

	SharedPreferences sharedPreferences;

	@Override
	public void sauvegarder() {
	}

	public SessionParametres(Context context) {
		super(context);
		this.sharedPreferences = getContext().getSharedPreferences(PREFERENCES,
				0);
	}

	@Override
	public void setType(TypeHorloge typeHorloge) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(TYPE, typeHorloge.toString());
		editor.commit();
	}

	@Override
	public TypeHorloge getType() {
		return TypeHorloge.valueOf(this.sharedPreferences.getString(TYPE,
				TypeHorloge.CERCLE.toString()));
	}

	@Override
	public boolean isAfficherFond() {
		return this.sharedPreferences.getBoolean(AFFICHER_FOND, true);
	}

	@Override
	public void setAfficherFond(boolean afficherFond) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(AFFICHER_FOND, afficherFond);
		editor.commit();
	}

	@Override
	public boolean isAfficherJours() {
		return this.sharedPreferences.getBoolean(AFFICHER_JOURS, true);
	}

	@Override
	public void setAfficherJours(boolean afficherJours) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(AFFICHER_JOURS, afficherJours);
		editor.commit();
	}

	@Override
	public int getCouleurAujourdhui() {
		return this.sharedPreferences.getInt(COULEUR_AUJOURDHUI, Color.YELLOW);
	}

	@Override
	public void setCouleurAujourdhui(int couleurAujourdhui) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(COULEUR_AUJOURDHUI, couleurAujourdhui);
		editor.commit();
	}

	@Override
	public int getCouleurJours() {
		return this.sharedPreferences.getInt(COULEUR_JOURS, Color.BLACK);
	}

	@Override
	public void setCouleurJours(int couleurJours) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(COULEUR_JOURS, couleurJours);
		editor.commit();
	}

	@Override
	public int getIntervalleCalendrierMois() {
		return this.sharedPreferences.getInt(NOMBRE_MOIS, 2);
	}

	@Override
	public void setIntervalleCalendrierMois(int nbMois) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(NOMBRE_MOIS, nbMois);
		editor.commit();
	}

	@Override
	public int getCouleurJoursAvecBarreDeTemps() {
		return this.sharedPreferences.getInt(COULEUR_JOURS_AVEC_BARRE_DE_TEMPS,
				Color.parseColor("#fca3ee"));
	}

	@Override
	public void setCouleurJoursAvecBarreDeTemps(int couleurJoursAvecBarreDeTemps) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(COULEUR_JOURS_AVEC_BARRE_DE_TEMPS,
				couleurJoursAvecBarreDeTemps);
		editor.commit();
	}

	@Override
	public boolean isDessinerFondHeureDynamique() {
		return this.sharedPreferences.getBoolean(DESSINER_FOND_HEURE_DYNAMIQUE,
				false);
	}

	@Override
	public void setDessinerFondHeureDynamique(boolean dessinerFondHeureDynamique) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(DESSINER_FOND_HEURE_DYNAMIQUE,
				dessinerFondHeureDynamique);
		editor.commit();
	}

	@Override
	public void setOrientation(int orientation){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(ORIENTATION,
				orientation);
		editor.commit();
	}

	@Override
	public int getOrientation(){
		return this.sharedPreferences.getInt(ORIENTATION,
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

}
