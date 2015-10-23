package org.indiarose.indiarosetimebar.factory;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.indiarosetimebar.model.Periode;

import android.graphics.Color;

/**
 * Classe permettant de generer des periodes
 * @author florentchampigny
 *
 */
public class PeriodeFactory {

	/**
	 * Genere les periodes de fond de l'horloge (matin, midi, aprem, soir, nuit)
	 * @return les periodes de fond de l'horloge
	 */
	public static List<Periode> genererPeriodesFond() {
		List<Periode> periodes = new ArrayList<Periode>();

		Periode matin = new Periode("", Color.parseColor("#78C96A"), 7.5f, 12f);
		Periode midi = new Periode("", Color.parseColor("#D9D95E"), 12f, 14f);
		Periode aprem = new Periode("", Color.parseColor("#FF7A30"), 14f, 18f);
		Periode soir = new Periode("", Color.parseColor("#4278C9"), 18f, 20f);
		Periode nuit = new Periode("", Color.parseColor("#2E548C"), 20f, 7.5f);

		periodes.add(matin);
		periodes.add(midi);
		periodes.add(aprem);
		periodes.add(soir);
		periodes.add(nuit);

		return periodes;
	}

}
