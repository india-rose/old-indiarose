package org.indiarose.net;

import java.io.Serializable;

public class ActionLogSendModel implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String description;
	private String type;
	private String time;
	
	public ActionLogSendModel() {
	}
	
	public ActionLogSendModel(String description, String type, String time) {
		super();
		this.description = description;
		this.type = type;
		this.time = time;
		
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
