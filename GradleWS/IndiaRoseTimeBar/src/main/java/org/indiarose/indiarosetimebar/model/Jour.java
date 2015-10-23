package org.indiarose.indiarosetimebar.model;

import java.text.DateFormat;
import java.util.*;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Classe permettant de sauvegarder un jour (une date) associe a ses periodes
 * @author florentchampigny
 *
 */
public class Jour {
	static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);

	Date date; //La date (approximative, ne pas tenir compte de l'heure) de la journee

	List<Periode> periodes = new ArrayList<Periode>(); //La liste des periodes de la journee

	String indiagramPath = null; //L'image / l'indiagram qui represente cette journee

	public Jour() {
	}

	public Jour(Date date) {
		this.date = date;
	}

	public Jour(Date date, List<Periode> periodes) {
		this(date);
		this.periodes.addAll(periodes);
	}

	public String toString() {
		return dateFormat.format(date);
	}

	@JsonIgnore
	public String getJour() {
		return toString().split(" ")[0];
	}

	@JsonIgnore
	public String getNombre() {
		return toString().split(" ")[1];
	}

	@JsonIgnore
	public String getMois() {
		return toString().split(" ")[2];
	}

	@JsonIgnore
	public String getAnnee() {
		return toString().split(" ")[3];
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Periode> getPeriodes() {
		return periodes;
	}

	public void setPeriodes(List<Periode> periodes) {
		this.periodes = periodes;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			if (o instanceof Jour)
				return toString().equals(((Jour) o).toString());
			else if (o instanceof Date) {
				return dateFormat.format((Date) o).toString()
						.equals(this.toString());
			}
		}

		return false;
	}

	public void addPeriode(Periode periode) {
		this.periodes.add(periode);
	}

	public String getIndiagramPath() {
		return indiagramPath;
	}

	public void setIndiagramPath(String indiagramPath) {
		this.indiagramPath = indiagramPath;
	}

}
