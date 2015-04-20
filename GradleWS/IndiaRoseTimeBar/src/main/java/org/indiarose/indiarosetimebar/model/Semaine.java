package org.indiarose.indiarosetimebar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe representant une semaine, donc une suite de jours
 * @author florentchampigny
 *
 */
public class Semaine {

	List<Jour> jours = new ArrayList<Jour>();

	public List<Jour> getJours() {
		return jours;
	}

	public void setJours(List<Jour> jours) {
		this.jours = jours;
	}

	public void addJours(Jour... jours) {
		for (Jour jour : jours)
			this.jours.add(jour);
	}

	public Semaine() {
	}

	public Semaine(List<Jour> jours) {
		this.jours = jours;
	}

	@Override
	public String toString() {
		return "Semaine [jours=" + jours + "]";
	}

}
