package org.indiarose.indiarosetimer.modele;

public class TimerModele {

	private int id;
	private String name;
	private String path_consigne;
	private int typeChrono;
	private float timeSeconde;
	private int graduationTextColor;
	private int graduationTraitColor;
	private int colorPrincipal;
	private int colorSecondaire;

	public TimerModele(int id,int typeChrono, float timeSeconde,int graduationTextColor,int graduationTraitColor,int colorPrincipal, int colorSecondaire,String name,String path_consigne){
		this.id = id;
		this.typeChrono = typeChrono;
		this.timeSeconde = timeSeconde;
		this.graduationTextColor = graduationTextColor;
		this.graduationTraitColor = graduationTraitColor;
		this.colorPrincipal = colorPrincipal;
		this.colorSecondaire = colorSecondaire;
		this.name = name;
		this.path_consigne = path_consigne;
	}


	public TimerModele(int typeChrono, float timeSeconde,int graduationTextColor,int graduationTraitColor,int colorPrincipal, int colorSecondaire,String name,String path_consigne){
		this.typeChrono = typeChrono;
		this.timeSeconde = timeSeconde;
		this.graduationTextColor = graduationTextColor;
		this.graduationTraitColor = graduationTraitColor;
		this.colorPrincipal = colorPrincipal;
		this.colorSecondaire = colorSecondaire;
		this.name = name;
		this.path_consigne = path_consigne;
	}


	// ########################## GETTERS AND SETTERS ################################

	public int getId(){
		return this.id;
	}

	public int getTypeChrono() {
		return typeChrono;
	}

	public void setTypeChrono(int typeChrono) {
		this.typeChrono = typeChrono;
	}

	public float getTimeSeconde() {
		return timeSeconde;
	}

	public void setTimeSeconde(float timeSeconde) {
		this.timeSeconde = timeSeconde;
	}


	public int getGraduationTextColor() {
		return graduationTextColor;
	}


	public void setGraduationTextColor(int graduationTextColor) {
		this.graduationTextColor = graduationTextColor;
	}


	public int getGraduationTraitColor() {
		return graduationTraitColor;
	}


	public void setGraduationTraitColor(int graduationTraitColor) {
		this.graduationTraitColor = graduationTraitColor;
	}


	public int getColorPrincipal() {
		return colorPrincipal;
	}


	public void setColorPrincipal(int colorPrincipal) {
		this.colorPrincipal = colorPrincipal;
	}


	public int getColorSecondaire() {
		return colorSecondaire;
	}


	public void setColorSecondaire(int colorSecondaire) {
		this.colorSecondaire = colorSecondaire;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPath_consigne() {
		return path_consigne;
	}


	public void setPath_consigne(String path_consigne) {
		this.path_consigne = path_consigne;
	}

}
