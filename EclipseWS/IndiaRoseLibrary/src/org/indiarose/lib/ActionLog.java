package org.indiarose.lib;

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */

import java.io.Serializable;

public class ActionLog implements Serializable{
	/**
	 * generated serial Version UID
	 */
	private static final long serialVersionUID = -5687476837716901813L;
	public static final String TYPE_VALIDER="valider";
	public static final String TYPE_INDIAGRAM="indiagram";
	public static final String TYPE_NEXT="next";
	public static final String TYPE_CORRECTION="correction";
	public static final String TYPE_CATEGORY="category";
	public static final String TYPE_SUPPRESSION="suppression";
	public String email;
	private String description;
	private String type;
	private String time;
	
	public ActionLog() {
	}

	public ActionLog(String description, String type, String time,String email) {
		super();
		this.description = description;
		this.type = type;
		this.time = time;
		this.email=email;
		
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
	
	

}
