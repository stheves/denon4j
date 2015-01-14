package de.theves.denon4j;


public class Response {
	private String response;

	public Response(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

	@Override
	public String toString() {
		return "Response [response=" + response + "]";
	}
}
