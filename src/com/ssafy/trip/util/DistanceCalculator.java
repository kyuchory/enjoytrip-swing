package com.ssafy.trip.util;

public final class DistanceCalculator {

	private static final double EARTH_RADIUS_KM = 6371.0088;

	private DistanceCalculator() {
	}

	public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
		double latDistance = Math.toRadians(lat2 - lat1);
		double lngDistance = Math.toRadians(lng2 - lng1);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}
}
