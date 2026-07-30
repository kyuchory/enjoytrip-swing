package com.ssafy.trip.util;

import java.util.ArrayList;
import java.util.List;

public final class StoreCsvParser {

	private StoreCsvParser() {
	}

	/**
	 * 따옴표로 감싼 필드, 필드 내부 쉼표, 이중 따옴표를 처리한다.
	 */
	public static List<String> parseLine(String line) {
		List<String> values = new ArrayList<String>();
		StringBuilder value = new StringBuilder();
		boolean quoted = false;

		for (int i = 0; i < line.length(); i++) {
			char current = line.charAt(i);
			if (current == '"') {
				if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
					value.append('"');
					i++;
				} else {
					quoted = !quoted;
				}
			} else if (current == ',' && !quoted) {
				values.add(value.toString());
				value.setLength(0);
			} else {
				value.append(current);
			}
		}
		values.add(value.toString());
		return values;
	}
}
