package org.indiarose.indiarosetimebar.parametres;

import org.indiarose.indiarosetimebar.canvas.horloge.TypeHorloge;
import org.indiarose.indiarosetimebar.parametres.session.SessionParametres;

import android.content.Context;

/**
 * Classe abstraite donnant la possibilit� de gerer la sauvegarde des parametres de l'application
 * A implementer selon la fa�on dont nous voulons sauvegarder nos parametres
 * @author florentchampigny
 *
 */
public abstract class Parametres {

	Context context;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Parametres(Context context) {
		this.context = context;
	}

	public abstract TypeHorloge getType();

	public abstract void setType(TypeHorloge type);

	public abstract boolean isAfficherFond();

	public abstract void setAfficherFond(boolean afficherFond);

	public abstract boolean isAfficherJours();

	public abstract void setAfficherJours(boolean afficherJours);

	public abstract int getCouleurAujourdhui();

	public abstract void setCouleurAujourdhui(int couleurAujourdhui);

	public abstract int getCouleurJours();

	public abstract void setCouleurJours(int couleurJours);

	public abstract int getCouleurJoursAvecBarreDeTemps();

	public abstract void setCouleurJoursAvecBarreDeTemps(
			int couleurJoursAvecBarreDeTemps);

	public abstract boolean isDessinerFondHeureDynamique();

	public abstract void setDessinerFondHeureDynamique(
			boolean dessinerFondHeureDynamique);

	public abstract int getIntervalleCalendrierMois();

	public abstract void setIntervalleCalendrierMois(int nbMois);

	public abstract void setOrientation(int orientation);
	public abstract int getOrientation();
	
	/**
	 * Retourne l'implementation couremment utilisee de la sauvegarde de parametres
	 */
	public static Parametres getInstance(Context context) {
		return new SessionParametres(context);
	}
	
	/**
	 * A appeller pour sauvegarder les parametres
	 */
	public abstract void sauvegarder();

}
