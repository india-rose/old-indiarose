package org.indiarose.indiarosetimebar.persistance;

import org.indiarose.indiarosetimebar.model.Donnees;
import org.indiarose.indiarosetimebar.persistance.json.JSonPersistance;

import android.content.Context;

/**
 * Classe abstraite donnant la possibilit� de gerer la sauvegarde des donnees de l'application
 * A implementer selon la fa�on dont nous voulons sauvegarder nos donnees
 * @author florentchampigny
 *
 */
public abstract class Persistance {

	Context context;
	static Persistance persistance = null;

	protected Persistance(Context context) {
		this.context = context;
	}

	/**
	 * A appeller pour charger les donnees
	 * @return les donnes
	 */
	public abstract Donnees chargerDonnees();

	/**
	 * A appeller pour sauvegarder les donnees
	 * @return les donnes
	 */
	public abstract void sauvegarder(Donnees donnees);

	/**
	 * Retourne l'implementation couremment utilisee de la sauvegarde de donnees
	 */
	public static Persistance getInstance(Context context) {
		if (persistance == null)
			persistance = new JSonPersistance(context);
		return persistance;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static Persistance getPersistance() {
		return persistance;
	}

	public static void setPersistance(Persistance persistance) {
		Persistance.persistance = persistance;
	}

}
