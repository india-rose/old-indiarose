package org.indiarose.indiarosetimebar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Donnees qui seront sauvegardees
 * @author florentchampigny
 *
 */
public class Donnees {

	private List<Jour> jours = new ArrayList<Jour>(); //La liste des jours sauvegardes
	private List<Modele> modeles = new ArrayList<Modele>();  //La liste des modeles sauvegardes

	public Donnees() {

	}

	public List<Jour> getJours() {
		return jours;
	}

	public void setJours(List<Jour> jours) {
		this.jours = jours;
	}

	public List<Modele> getModeles() {
		return modeles;
	}
	
	public void setModeles(List<Modele> modeles) {
		this.modeles = modeles;
	}

	public String toString() {
		return jours.toString() + " \n " + modeles.toString();
	}

}
