package org.indiarose.indiarosetimebar.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.indiarose.api.IndiagramManager;
import org.indiarose.indiarosetimebar.factory.CouleurFactory;
import org.indiarose.lib.model.Indiagram;

import android.graphics.Bitmap;

/**
 * Une periode est l'association entre 2 heures et un nom
 * Accessoirement, nous pouvons lui associer un Indiagram, et une couleur de fond
 * Cette periode sera affichee sur l'horloge
 * @author florentchampigny
 *
 */
public class Periode {

	String nom = "";

	int couleur = CouleurFactory.getCouleurAleatoire();

	Heure heureDebut = new Heure(0);

	Heure heureFin = new Heure(0);

	String indiagramPath;

	boolean afficherFondCouleur = true;

	@JsonIgnore
	Indiagram indiagram = null;
	@JsonIgnore
	Bitmap indiagramBitmap = null;

	public Periode() {
	}

	public Periode(String nom, int couleur, Heure heureDebut, Heure heureFin) {
		super();
		this.nom = nom;
		this.couleur = couleur;
		this.heureDebut = heureDebut;
		this.heureFin = heureFin;
	}

	public Periode(String nom, int couleur, float heureDebut, float heureFin) {
		this(nom, couleur, new Heure(heureDebut), new Heure(heureFin));
	}

	public boolean contains(float heure) {
		return heureDebut.getHeure() <= heureFin.getHeure() ? heureDebut
				.getHeure() <= heure && heure <= heureFin.getHeure()
				: (heureDebut.getHeure() <= heure && heure <= 24f)
						|| (0 <= heure && heure <= heureFin.getHeure());
	}

	public boolean chargerIndiagram(IndiagramManager indiagramManager, boolean reload) {
		if (indiagramPath != null && indiagramManager != null) {
			if(reload)
				indiagramBitmap = null;
			
			if (indiagramBitmap == null) {
				this.indiagram = indiagramManager
						.getIndiagramByPath(indiagramPath);

				if (indiagram != null)
					this.indiagramBitmap = this.indiagram.getImageAsBitmap();
			}
		}
		
		return (indiagram != null);
	}
	
	public boolean chargerIndiagram(IndiagramManager indiagramManager) {
		return chargerIndiagram(indiagramManager,false);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getCouleur() {
		return couleur;
	}

	public void setCouleur(int couleur) {
		this.couleur = couleur;
	}

	public Heure getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(Heure heureDebut) {
		this.heureDebut = heureDebut;
	}

	public Heure getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(Heure heureFin) {
		this.heureFin = heureFin;
	}

	public String getIndiagramPath() {
		return indiagramPath;
	}

	public void setIndiagramPath(String indiagramPath) {
		this.indiagramPath = indiagramPath;
	}

	public Indiagram getIndiagram() {
		return indiagram;
	}

	public void setIndiagram(Indiagram indiagram) {
		this.indiagram = indiagram;
	}

	public Bitmap getIndiagramBitmap() {
		return indiagramBitmap;
	}

	public void setIndiagramBitmap(Bitmap indiagramBitmap) {
		this.indiagramBitmap = indiagramBitmap;
	}

	public boolean isAfficherFondCouleur() {
		return afficherFondCouleur;
	}

	public void setAfficherFondCouleur(boolean afficherFondCouleur) {
		this.afficherFondCouleur = afficherFondCouleur;
	}

	@Override
	public String toString() {
		return "Periode [nom=" + nom + ", couleur=" + couleur + ", heureDebut="
				+ heureDebut + ", heureFin=" + heureFin + ", indiagramPath="
				+ indiagramPath + ", indiagram=" + indiagram
				+ ", indiagramBitmap=" + indiagramBitmap + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((heureDebut == null) ? 0 : heureDebut.hashCode());
		result = prime * result
				+ ((heureFin == null) ? 0 : heureFin.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		Periode other = (Periode) obj;
		if (heureDebut == null) {
			if (other.heureDebut != null)
				return false;
		} else if (!heureDebut.equals(other.heureDebut))
			return false;
		if (heureFin == null) {
			if (other.heureFin != null)
				return false;
		} else if (!heureFin.equals(other.heureFin))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (indiagram == null) {
			if (other.indiagram != null)
				return false;
		} else if (!indiagram.equals(other.indiagram))
			return false;
		return true;
	}
}
