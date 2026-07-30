package com.ssafy.trip.model.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ssafy.trip.model.dao.StoreDao;
import com.ssafy.trip.model.dao.StoreDaoImpl;
import com.ssafy.trip.model.dto.StoreDto;
import com.ssafy.trip.model.dto.StoreSearchDto;
import com.ssafy.trip.model.dto.TripDto;

public class StoreServiceImpl implements StoreService {

	private static final Map<String, String> REGION_KEYWORDS = createRegionKeywords();

	private final StoreDao storeDao;

	public StoreServiceImpl() {
		storeDao = new StoreDaoImpl();
	}

	@Override
	public List<StoreDto> searchNearby(TripDto trip, double radiusKm, String category, int limit) throws IOException {
		if (trip == null) {
			throw new IllegalArgumentException("관광지 정보가 없습니다.");
		}
		if (trip.getLat() == 0 || trip.getLng() == 0) {
			throw new IllegalArgumentException("관광지 좌표가 없습니다.");
		}

		StoreSearchDto searchDto = new StoreSearchDto();
		searchDto.setRegion(resolveRegion(trip));
		searchDto.setCategory(category);
		searchDto.setLat(trip.getLat());
		searchDto.setLng(trip.getLng());
		searchDto.setRadiusKm(radiusKm);
		searchDto.setLimit(limit);
		return storeDao.searchNearby(searchDto);
	}

	private String resolveRegion(TripDto trip) throws IOException {
		String address = trip.getStreetAddress();
		if (address == null || address.trim().isEmpty()) {
			address = trip.getLotAddress();
		}
		if (address != null) {
			for (Map.Entry<String, String> entry : REGION_KEYWORDS.entrySet()) {
				if (address.startsWith(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		throw new IOException("관광지 주소에서 시·도를 확인할 수 없습니다.");
	}

	private static Map<String, String> createRegionKeywords() {
		Map<String, String> regions = new LinkedHashMap<String, String>();
		regions.put("서울", "서울");
		regions.put("부산", "부산");
		regions.put("대구", "대구");
		regions.put("인천", "인천");
		regions.put("광주", "광주");
		regions.put("대전", "대전");
		regions.put("울산", "울산");
		regions.put("세종", "세종");
		regions.put("경기", "경기");
		regions.put("강원", "강원");
		regions.put("충청북", "충북");
		regions.put("충북", "충북");
		regions.put("충청남", "충남");
		regions.put("충남", "충남");
		regions.put("전라북", "전북");
		regions.put("전북", "전북");
		regions.put("전라남", "전남");
		regions.put("전남", "전남");
		regions.put("경상북", "경북");
		regions.put("경북", "경북");
		regions.put("경상남", "경남");
		regions.put("경남", "경남");
		regions.put("제주", "제주");
		return regions;
	}
}
