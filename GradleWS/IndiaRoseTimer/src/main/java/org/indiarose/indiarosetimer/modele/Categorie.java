package org.indiarose.indiarosetimer.modele;

import java.util.ArrayList;
import java.util.List;

import org.indiarose.lib.model.Indiagram;

public class Categorie {

	private int id;
	private int type_categorie;
	private Indiagram indiagram;
	private List<TimerModele> timers = new ArrayList<TimerModele>();
	 
	public static int TYPE_TIMER = 0;
	public static int TYPE_BARRE_TACHE = 1;
	
	public Categorie(){
		
	}
	
	public Categorie(int id,Indiagram indiagram,int type_categorie){
		this.id = id;
		this.indiagram = indiagram;
		this.type_categorie = type_categorie;
	}
	
	public Categorie(Indiagram indiagram,int type_categorie){
		this(-1,indiagram,type_categorie);
	}
	
	public Categorie(int id,Indiagram indiagram,int type_categorie,List<TimerModele> timers){
		this(id,indiagram,type_categorie);
		this.timers = timers;
	}
	
	// #################### Getters And Setters #################

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public Indiagram getIndiagram() {
		return indiagram;
	}

	public void setIndiagram(Indiagram indiagram) {
		this.indiagram = indiagram;
	}

	public int getType_categorie() {
		return type_categorie;
	}

	public void setType_categorie(int type_categorie) {
		this.type_categorie = type_categorie;
	}

	public List<TimerModele> getTimers() {
		return timers;
	}

	public void setTimers(List<TimerModele> timers) {
		this.timers = timers;
	}
	
}
