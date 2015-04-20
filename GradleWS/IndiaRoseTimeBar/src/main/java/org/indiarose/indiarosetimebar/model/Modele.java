package org.indiarose.indiarosetimebar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant d'enregistrer un "jour type" dans la base
 * Ce jour aura un nom, et une lsite de periodes
 * @author florentchampigny
 *
 */
public class Modele {
	String nom = "";
	List<Periode> periodes = new ArrayList<Periode>();

	public Modele() {
	}

	public Modele(String nom, List<Periode> periodes) {
		this.nom = nom;
		this.periodes.addAll(periodes);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public List<Periode> getPeriodes() {
		return periodes;
	}

	public void setPeriodes(List<Periode> periodes) {
		this.periodes = periodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result
				+ ((periodes == null) ? 0 : periodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Modele other = (Modele) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (periodes == null) {
			if (other.periodes != null)
				return false;
		} else if (!periodes.equals(other.periodes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Modele [nom=" + nom + "periodes=" + periodes + " ]";
	}

}
