package de.theves.denon4j;

import java.util.ArrayList;
import java.util.List;

class ResponseParser {
	private char delimiter;

	ResponseParser(char delemiter) {
		this.delimiter = delemiter;
	}

	Response parseResponse(byte[] response) {
		// convert to response
		List<String> lines = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (byte b : response) {
			if (delimiter == (char) b) {
				lines.add(line.toString());
				line.delete(0, line.length());
			} else {
				line.append((char) b);
			}
		}
		return new Response(lines);
	}
}
