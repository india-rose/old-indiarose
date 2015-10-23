package org.indiarose.indiarosetimebar.factory;

import java.util.Random;

import android.graphics.Color;

/**
 * Classe permetant de generer des couleurs
 * @author florentchampigny
 *
 */
public class CouleurFactory {

	/**
	 * Genere une couleur aleatoire
	 * @return une couleur aleatoire
	 */
	public static int getCouleurAleatoire() {
		Random rnd = new Random();
		int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256));
		return color;
	}

}
