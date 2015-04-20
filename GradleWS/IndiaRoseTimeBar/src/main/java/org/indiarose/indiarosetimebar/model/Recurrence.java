package org.indiarose.indiarosetimebar.model;

import java.util.Date;

public class Recurrence {
	public enum TypeRecurrence{
		ALLDAYS,
		ALLDAYSOFWEEK,
		ALLWEEKS,
		ALLMONTH,
		ALLYEARS
	};

	public enum FinRecurrence{
		ALWAYS,
		UNTIL,
		OCCURENCE
	};
	
	private Date dateDebut;
	private Date dateFin;
	private TypeRecurrence type;
	private FinRecurrence fin;
	private int nbserie;
	private int interval;
	
	public Recurrence (Date dateDebut, Date dateFin, TypeRecurrence typeRecurrence, FinRecurrence finRecurrence, int nbserie, int interval){
		this.dateDebut= dateDebut;
		this.dateFin = dateFin;
		this.type = typeRecurrence;
		this.fin = finRecurrence;
		this.nbserie = nbserie;
		this.interval = interval;
	}
	
	public Date getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}
	public Date getDateFin() {
		return dateFin;
	}
	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}
	public TypeRecurrence getType() {
		return type;
	}
	public void setType(TypeRecurrence type) {
		this.type = type;
	}
	public FinRecurrence getFin() {
		return fin;
	}
	public void setFin(FinRecurrence fin) {
		this.fin = fin;
	}
	public int getNbserie() {
		return nbserie;
	}
	public void setNbserie(int nbserie) {
		this.nbserie = nbserie;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
}
