package com.example.blooddonationapp.ModelClasses.NearbySearch;

import java.util.List;

public class NearbyPlaceResponse {
	private List<Results> results;
	private String status;

	public NearbyPlaceResponse() {

	}

	public List<Results> getResults() {
		return this.results;
	}

	public String getStatus() {
		return this.status;
	}

	public void setResults(List<Results> value) {
		this.results = value;
	}

	public void setStatus(String value) {
		this.status = value;
	}

}