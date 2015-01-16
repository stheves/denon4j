package de.theves.denon4j;

import java.util.Collections;
import java.util.List;

public class Response {
	private Commands command;
	private Parameters parameter;
	private String value;
	private List<String> responseLines;

	public Response(List<String> response) {
		this.responseLines = response;
	}

	public List<String> getResponse() {
		return Collections.unmodifiableList(responseLines);
	}

	@Override
	public String toString() {
		return responseLines.toString();
	}
}
