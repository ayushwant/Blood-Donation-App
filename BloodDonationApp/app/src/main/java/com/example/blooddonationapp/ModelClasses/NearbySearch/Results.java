package com.example.blooddonationapp.ModelClasses.NearbySearch;

import java.util.List;

public class Results {
	private String business_status;   // need to replace businessStatus to business_status, exactly as it is in json
	private Geometry geometry;
	private String icon;
	private String iconBackgroundColor;
	private String iconMaskBaseUri;
	private String name;
	private opening_hours opening_hours;
	private List<Photos> photos;
	private String place_id;
	private PlusCode plusCode;
	private int priceLevel;
	private float rating;
	private String reference;

	public String getBusinessStatus() {
		return business_status;
	}

	public void setBusinessStatus(String business_status) {
		this.business_status = business_status;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconBackgroundColor() {
		return iconBackgroundColor;
	}

	public void setIconBackgroundColor(String iconBackgroundColor) {
		this.iconBackgroundColor = iconBackgroundColor;
	}

	public String getIconMaskBaseUri() {
		return iconMaskBaseUri;
	}

	public void setIconMaskBaseUri(String iconMaskBaseUri) {
		this.iconMaskBaseUri = iconMaskBaseUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public opening_hours getOpeningHours() {
		return opening_hours;
	}

	public void setOpeningHours(opening_hours opening_hours) {
		this.opening_hours = opening_hours;
	}

	public List<Photos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photos> photos) {
		this.photos = photos;
	}

	public String getPlaceId() {
		return place_id;
	}

	public void setPlaceId(String place_id) {
		this.place_id = place_id;
	}

	public PlusCode getPlusCode() {
		return plusCode;
	}

	public void setPlusCode(PlusCode plusCode) {
		this.plusCode = plusCode;
	}

	public int getPriceLevel() {
		return priceLevel;
	}

	public void setPriceLevel(int priceLevel) {
		this.priceLevel = priceLevel;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public int getUserRatingsTotal() {
		return user_ratings_total;
	}

	public void setUserRatingsTotal(int user_ratings_total) {
		this.user_ratings_total = user_ratings_total;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	private String scope;
	private List<String> types;
	private int user_ratings_total;
	private String vicinity;

	public Results() {

	}

}