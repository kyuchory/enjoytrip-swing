package com.ssafy.trip.model.service;

import java.io.IOException;
import java.util.List;

import com.ssafy.trip.model.dto.StoreDto;
import com.ssafy.trip.model.dto.TripDto;

public interface StoreService {

	List<StoreDto> searchNearby(TripDto trip, double radiusKm, String category, int limit) throws IOException;
}
