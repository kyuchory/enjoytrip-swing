package com.ssafy.trip.model.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Stream;

import com.ssafy.trip.model.dto.StoreDto;
import com.ssafy.trip.model.dto.StoreSearchDto;
import com.ssafy.trip.util.DistanceCalculator;
import com.ssafy.trip.util.StoreCsvParser;

public class StoreDaoImpl implements StoreDao {

	private static final Path STORE_DIRECTORY = Paths.get("res", "arround");
	private static final String ALL_CATEGORY = "전체";

	@Override
	public List<StoreDto> searchNearby(StoreSearchDto searchDto) throws IOException {
		Path csvPath = findRegionCsv(searchDto.getRegion());
		int limit = Math.max(1, searchDto.getLimit());
		PriorityQueue<StoreDto> nearestStores = new PriorityQueue<StoreDto>(
				limit, Comparator.comparingDouble(StoreDto::getDistance).reversed());

		double latRange = searchDto.getRadiusKm() / 111.0;
		double longitudeScale = Math.cos(Math.toRadians(searchDto.getLat()));
		double lngRange = longitudeScale == 0 ? 180.0 : searchDto.getRadiusKm() / (111.0 * longitudeScale);

		try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
			String headerLine = reader.readLine();
			if (headerLine == null) {
				return Collections.emptyList();
			}

			Map<String, Integer> columns = createColumnMap(StoreCsvParser.parseLine(removeBom(headerLine)));
			validateRequiredColumns(columns);

			String line;
			while ((line = reader.readLine()) != null) {
				if (Thread.currentThread().isInterrupted()) {
					return Collections.emptyList();
				}

				List<String> values = StoreCsvParser.parseLine(line);
				StoreDto store = toStore(values, columns);
				if (store == null || !matchesCategory(store, searchDto.getCategory())) {
					continue;
				}
				if (Math.abs(store.getLat() - searchDto.getLat()) > latRange
						|| Math.abs(store.getLng() - searchDto.getLng()) > lngRange) {
					continue;
				}

				double distance = DistanceCalculator.distanceKm(
						searchDto.getLat(), searchDto.getLng(), store.getLat(), store.getLng());
				if (distance > searchDto.getRadiusKm()) {
					continue;
				}

				store.setDistance(distance);
				nearestStores.offer(store);
				if (nearestStores.size() > limit) {
					nearestStores.poll();
				}
			}
		}

		List<StoreDto> result = new ArrayList<StoreDto>(nearestStores);
		Collections.sort(result, Comparator.comparingDouble(StoreDto::getDistance));
		return result;
	}

	private Path findRegionCsv(String region) throws IOException {
		if (region == null || region.trim().isEmpty()) {
			throw new IOException("관광지 주소에서 지역을 확인할 수 없습니다.");
		}
		if (!Files.isDirectory(STORE_DIRECTORY)) {
			throw new IOException("상권 데이터 디렉터리를 찾을 수 없습니다: " + STORE_DIRECTORY);
		}

		try (Stream<Path> paths = Files.list(STORE_DIRECTORY)) {
			Optional<Path> match = paths
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().toLowerCase().endsWith(".csv"))
					.filter(path -> path.getFileName().toString().contains(region))
					.findFirst();
			if (match.isPresent()) {
				return match.get();
			}
		}
		throw new IOException(region + " 지역의 상권 CSV 파일을 찾을 수 없습니다.");
	}

	private Map<String, Integer> createColumnMap(List<String> header) {
		Map<String, Integer> columns = new HashMap<String, Integer>();
		for (int i = 0; i < header.size(); i++) {
			columns.put(header.get(i), i);
		}
		return columns;
	}

	private void validateRequiredColumns(Map<String, Integer> columns) throws IOException {
		String[] required = {
				"상가업소번호", "상호명", "상권업종대분류명", "상권업종중분류명",
				"상권업종소분류명", "도로명주소", "경도", "위도"
		};
		for (String column : required) {
			if (!columns.containsKey(column)) {
				throw new IOException("상권 CSV에 필수 컬럼이 없습니다: " + column);
			}
		}
	}

	private StoreDto toStore(List<String> values, Map<String, Integer> columns) {
		try {
			String latValue = getValue(values, columns, "위도");
			String lngValue = getValue(values, columns, "경도");
			if (latValue.isEmpty() || lngValue.isEmpty()) {
				return null;
			}

			StoreDto store = new StoreDto();
			store.setStoreId(getValue(values, columns, "상가업소번호"));
			store.setStoreName(getValue(values, columns, "상호명"));
			store.setBranchName(getValue(values, columns, "지점명"));
			store.setCategoryLarge(getValue(values, columns, "상권업종대분류명"));
			store.setCategoryMiddle(getValue(values, columns, "상권업종중분류명"));
			store.setCategorySmall(getValue(values, columns, "상권업종소분류명"));
			store.setAddress(getValue(values, columns, "도로명주소"));
			store.setLng(Double.parseDouble(lngValue));
			store.setLat(Double.parseDouble(latValue));
			return store;
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			return null;
		}
	}

	private String getValue(List<String> values, Map<String, Integer> columns, String column) {
		Integer index = columns.get(column);
		if (index == null || index < 0 || index >= values.size()) {
			return "";
		}
		return values.get(index).trim();
	}

	private boolean matchesCategory(StoreDto store, String category) {
		return category == null || category.isEmpty() || ALL_CATEGORY.equals(category)
				|| category.equals(store.getCategoryLarge());
	}

	private String removeBom(String value) {
		return value.startsWith("\uFEFF") ? value.substring(1) : value;
	}
}
