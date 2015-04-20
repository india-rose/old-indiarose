package org.indiarose.indiarosetimebar.model;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Classe permettant de transformer un float en Heure
 * @author florentchampigny
 *
 */
public class Heure {

	float heure;

	private static final String sparator = ":";

	public Heure() {
	}

	public Heure(float heure) {
		this.heure = heure;
	}

	public Heure(String heure) {
		this.heure = heureTofloat(heure);
	}

	public Heure(int heure, int minute) {
		this.heure = heureTofloat(encode(heure, minute));
	}

	@JsonIgnore
	public int getHeureInt() {
		return (int) heure;
	}

	@JsonIgnore
	public int getMinute() {
		return (int) ((heure - getHeureInt()) * 60);
	}

	public String toString() {
		return encode(getHeureInt(), getMinute());
	}

	public static String encode(int heure, int minute) {
		return (heure > 24 ? heure - 24 : heure) + sparator + minute;
	}

	public static float heureTofloat(String heure) {

		String[] heureDecoupee = heure.split(sparator);

		float heures = Float.parseFloat(heureDecoupee[0]);
		float minutes = Float.parseFloat(heureDecoupee[1]) / 60;

		return heures + minutes;
	}

	public float getHeure() {
		return heure;
	}

	public void setHeure(float heure) {
		this.heure = heure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(heure);
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
		Heure other = (Heure) obj;
		if (Float.floatToIntBits(heure) != Float.floatToIntBits(other.heure))
			return false;
		return true;
	}
}
