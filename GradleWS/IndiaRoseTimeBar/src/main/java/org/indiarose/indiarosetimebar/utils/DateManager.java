package org.indiarose.indiarosetimebar.utils;

import java.util.*;

import org.indiarose.indiarosetimebar.model.Jour;

public class DateManager {

	/**
	 * Recupere la position d'aujourd'hui de la semaine
	 * Ex: Si aujourd'hui = Mardi, retourne 1
	 */
	public static int recupererJourDeCetteSemaine() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK,
				calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
		calendar.setTime(new Date());
		int jour = calendar.get(Calendar.DAY_OF_WEEK);

		int j = -1;

		switch (jour) {
		case Calendar.MONDAY:
			j = 0;
			break;
		case Calendar.TUESDAY:
			j = 1;
			break;
		case Calendar.WEDNESDAY:
			j = 2;
			break;
		case Calendar.THURSDAY:
			j = 3;
			break;
		case Calendar.FRIDAY:
			j = 4;
			break;
		case Calendar.SATURDAY:
			j = 5;
			break;
		case Calendar.SUNDAY:
			j = 6;
			break;
		}

		return j;
	}

	/**
	 * Retourne aujours'hui en tant que jour
	 */
	public static Jour getAujourdhui() {
		int j = recupererJourDeCetteSemaine();
		if (j < 0)
			return null;
		else
			return new Jour(recupererSemaineCourrante().get(j));
	}

	/**
	 * Retorune la liste des dates de la semaine courante
	 */
	public static List<Date> recupererSemaineCourrante() {
		return recupererSemaineDeCetteDate(new Date());
	}

	/**
	 * Retorune la liste des dates de la semaine d'un jour donne
	 */
	public static List<Date> recupererSemaineDeCetteDate(Date date) {
		List<Date> semaine = new ArrayList<Date>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		int jour = calendar.get(Calendar.DAY_OF_WEEK);
		int jourDeLaSemaine = -1;

		switch (jour) {
		case Calendar.MONDAY:
			jourDeLaSemaine = 0;
			break;
		case Calendar.TUESDAY:
			jourDeLaSemaine = 1;
			break;
		case Calendar.WEDNESDAY:
			jourDeLaSemaine = 2;
			break;
		case Calendar.THURSDAY:
			jourDeLaSemaine = 3;
			break;
		case Calendar.FRIDAY:
			jourDeLaSemaine = 4;
			break;
		case Calendar.SATURDAY:
			jourDeLaSemaine = 5;
			break;
		case Calendar.SUNDAY:
			jourDeLaSemaine = 6;
			break;
		}

		for (int i = 0; i <= 6; ++i) {

			int dist = 0;
			if (jourDeLaSemaine > i)
				dist = -1 * (jourDeLaSemaine - i);
			else
				dist = i - jourDeLaSemaine;

			calendar.setTime(date);
			calendar.add(Calendar.DATE, dist);
			semaine.add(calendar.getTime());
		}

		return semaine;
	}

	/**
	 * Retourne l'heure actuelle en float
	 */
	public static float getHeure() {
		java.util.GregorianCalendar calendar = new GregorianCalendar();
		int heure = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		int minute = calendar.get(java.util.Calendar.MINUTE);

		return (float) heure + (float) minute / 60.0f;
	}

}
