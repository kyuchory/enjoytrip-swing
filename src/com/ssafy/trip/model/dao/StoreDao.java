package com.ssafy.trip.model.dao;

import java.io.IOException;
import java.util.List;

import com.ssafy.trip.model.dto.StoreDto;
import com.ssafy.trip.model.dto.StoreSearchDto;

public interface StoreDao {

	List<StoreDto> searchNearby(StoreSearchDto searchDto) throws IOException;
}
