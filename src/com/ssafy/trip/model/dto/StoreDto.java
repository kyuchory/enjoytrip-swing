package com.ssafy.trip.model.dto;

public class StoreDto {

	private String storeId;
	private String storeName;
	private String branchName;
	private String categoryLarge;
	private String categoryMiddle;
	private String categorySmall;
	private String address;
	private double lng;
	private double lat;
	private double distance;

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCategoryLarge() {
		return categoryLarge;
	}

	public void setCategoryLarge(String categoryLarge) {
		this.categoryLarge = categoryLarge;
	}

	public String getCategoryMiddle() {
		return categoryMiddle;
	}

	public void setCategoryMiddle(String categoryMiddle) {
		this.categoryMiddle = categoryMiddle;
	}

	public String getCategorySmall() {
		return categorySmall;
	}

	public void setCategorySmall(String categorySmall) {
		this.categorySmall = categorySmall;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
