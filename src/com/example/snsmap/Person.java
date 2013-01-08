package com.example.snsmap;

import com.google.android.maps.GeoPoint;

public class Person {
	
	private GeoPoint gp;
	private int iconNumber;
	private String message;
	private String name;
	private String date;
	private String uuid;
	
	public void setGp(GeoPoint gp) {
		this.gp = gp;
	}

	public void setIconNumber(int iconNumber) {
		this.iconNumber = iconNumber;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public GeoPoint getGp() {
		return gp;
	}

	public int getIconNumber() {
		return iconNumber;
	}

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public String getUuid() {
		return uuid;
	}
}
