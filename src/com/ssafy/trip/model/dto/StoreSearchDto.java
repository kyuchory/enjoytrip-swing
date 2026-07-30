package com.ssafy.trip.model.dto;

public class StoreSearchDto {

	private String region;
	private String category;
	private double lat;
	private double lng;
	private double radiusKm;
	private int limit;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getRadiusKm() {
		return radiusKm;
	}

	public void setRadiusKm(double radiusKm) {
		this.radiusKm = radiusKm;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
