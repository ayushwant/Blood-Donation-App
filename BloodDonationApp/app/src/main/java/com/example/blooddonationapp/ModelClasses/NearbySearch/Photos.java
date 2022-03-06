package com.example.blooddonationapp.ModelClasses.NearbySearch;

import java.util.List;

public class Photos {
	private int height;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<String> getHtmlAttributions() {
		return htmlAttributions;
	}

	public void setHtmlAttributions(List<String> htmlAttributions) {
		this.htmlAttributions = htmlAttributions;
	}

	public String getPhotoReference() {
		return photoReference;
	}

	public void setPhotoReference(String photoReference) {
		this.photoReference = photoReference;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	private List<String> htmlAttributions;
	private String photoReference;
	private int width;

	public Photos() {

	}

}