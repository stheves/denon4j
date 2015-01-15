package de.theves.denon4j;

public class ConnectException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ConnectException(Throwable t) {
		super(t);
	}
}
