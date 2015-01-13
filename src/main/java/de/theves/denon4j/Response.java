package de.theves.denon4j;

import java.util.ArrayList;
import java.util.List;

public class Response {
	private byte[] response;

	public Response(byte[] responseBuffer) {
		this.response = responseBuffer;
	}

	// TODO make the status lines interpretable
	public List<String> getStatusLines() {
		List<String> statusLines = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		for (byte b : response) {
			if (Command.CR == (char) b) {
				statusLines.add(line.toString());
				line = new StringBuilder();
			} else {
				line.append((char) b);
			}
		}
		return statusLines;
	}

	@Override
	public String toString() {
		return "Response " + getStatusLines().toString();
	}

}
