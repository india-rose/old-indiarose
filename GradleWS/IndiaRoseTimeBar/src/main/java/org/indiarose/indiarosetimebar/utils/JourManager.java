package org.indiarose.indiarosetimebar.utils;

import java.util.*;

import org.indiarose.indiarosetimebar.model.*;

/**
 * Classe qui va gerer les jours, et surtout renvoyer le jour en fonction de la
 * date
 */

public class JourManager {

	public static Jour getJour(Donnees donnees, Date date) {

		if (donnees != null) {
			if (donnees.getJours() != null) {
				for (Jour i : donnees.getJours()) {
					if (i.equals(date)) {
						return i;
					}
				}
			}
		}
		Jour j = new Jour(date);
		donnees.getJours().add(j);
		return j;

	}

	public static List<Jour> getLesJoursDeCetteSemaine(Donnees donnees) {
		List<Jour> res = new ArrayList<Jour>();
		List<Date> dates = DateManager.recupererSemaineCourrante();
		for (Date date : dates) {
			res.add(JourManager.getJour(donnees, date));
		}
		return res;
	}

	public static List<Jour> getLesJoursDeLaSemaineDeCetteDate(Donnees donnees,
			Date date) {
		List<Jour> res = new ArrayList<Jour>();
		List<Date> dates = DateManager.recupererSemaineDeCetteDate(date);
		for (Date d : dates) {
			res.add(JourManager.getJour(donnees, d));
		}
		return res;
	}

	public static Semaine getSemaineDeCetteDate(Donnees donnees, Date date) {
		return new Semaine(getLesJoursDeLaSemaineDeCetteDate(donnees, date));
	}

	/**
	 * Donne l'indice d'aujourd'hui dans la semaine donnee
	 * Si non trouvee, retorune -1
	 */
	public static int trouverAujourdHui(Semaine semaine) {
		int i = 0;
		for (Jour j : semaine.getJours()) {
			if (DateManager.getAujourdhui().equals(j))
				return i;
			++i;
		}

		return -1;
	}

	public static List<Jour> getLesJours(Donnees donnees, List<Date> dates) {
		List<Jour> res = new ArrayList<Jour>();
		for (Date date : dates) {
			res.add(JourManager.getJour(donnees, date));
		}
		return res;
	}

	public static List<Periode> joursToPeriodes(List<Jour> jours) {
		List<Periode> periodes = new ArrayList<Periode>();

		if (jours != null) {
			for (Jour jour : jours)
				periodes.addAll(jour.getPeriodes());
		}
		return periodes;
	}

	/**
	 * Retourne vrai si tous les jours sont egaux
	 */
	public static boolean tousEgaux(List<Jour> jours) {
		if (jours != null) {
			if (jours.size() > 1) {
				List<Periode> periodes = jours.get(0).getPeriodes();
				for (Jour j : jours) {
					if (j.getPeriodes().size() != periodes.size()) {
						return false;
					}
					for (Periode p : j.getPeriodes()) {
						if (periodes.indexOf(p) == -1) {
							return false;
						}
					}
				}
				return true;
			} else if (jours.size() == 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retourne vrai si aucun jour n'a de periode
	 */
	public static boolean tousVides(List<Jour> jours) {

		for (Jour j : jours) {
			if (j.getPeriodes().size() > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retourne les jours au format date
	 */
	public static List<Date> joursToDates(List<Jour> jours) {
		List<Date> res = new ArrayList<Date>();
		for (Jour jour : jours) {
			if (jour.getPeriodes().size() > 0)
				res.add(jour.getDate());
		}
		return res;
	}

	/**
	 * Filtre les jours, ne retourne que ceux qui ont au moins une periode
	 */
	public static List<Jour> joursNonVides(List<Jour> jours) {
		List<Jour> js = new ArrayList<Jour>();
		for (Jour jour : jours) {
			if (jour.getPeriodes().size() > 0)
				js.add(jour);
		}
		return js;
	}

	/**
	 * Ne garde que les jours recents d'au moins "mois" mois
	 */
	public static List<Jour> joursAGarder(List<Jour> jours, int mois) {
		List<Jour> js = new ArrayList<Jour>();
		Date today = new Date(); 
		for (Jour jour : jours) {
			if (jour.getDate().getYear() <= today.getYear()){
				if(jour.getDate().getMonth() >= today.getMonth())
					js.add(jour);
				else if((today.getMonth() - jour.getDate().getMonth()) <= mois)
					js.add(jour);
			}

		}
		return js;
	}
}
