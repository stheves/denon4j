package de.theves.denon4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

final class Command {
	private static final char CR = 0x0d; // \r character
	private static final Charset ENCODING = Charset.forName("US-ASCII");

	private final String command;
	private final String parameter;
	private final int timeToWait;

	Command(String fullCommand) {
		this(fullCommand.substring(0, 2), fullCommand.substring(2,
				fullCommand.length()), 250);
	}

	Command(String command, String parameter, int timeToWait) {
		this.command = command;
		this.parameter = parameter;
		this.timeToWait = timeToWait;
	}

	/**
	 * Sends the command to the receiver (socket) and waits for a response
	 * (blocking).
	 *
	 * @param socket
	 *            the socket (not <code>null</code>).
	 * @param value
	 *            the value of the command to send (can be <code>null</code>).
	 * @return the response from the receiver. Only <code>null</code> if the
	 *         receiver didn`t sent a response for the command within the
	 *         <code>timeToWait</code> period. This may happen if a
	 *         <code>value</code> wasn`t changed actually.
	 * @throws IOException
	 */
	Response send(Socket socket, String value) throws IOException {
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		// send the command
		sendCommand(value, out);

		try {
			// receive the response
			return receiveResponse(in);
		} catch (SocketTimeoutException ste) {
			// no response within readTimeout
			return null;
		}
	}

	private void sendCommand(String value, OutputStream out) throws IOException {
		String request = buildRequest(value);
		out.write(request.getBytes(ENCODING));
		out.flush();
	}

	private Response receiveResponse(InputStream in) throws IOException {
		ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream(128);
		// read the stream as long as input is available (we never really
		// reach the end of the stream)
		byte[] buffer = new byte[128];
		int n = 0;
		while (-1 != (n = in.read(buffer))) {
			responseBuffer.write(buffer, 0, n);
			// as of specification
			waitForAvr();
			if (in.available() == 0) {
				break;
			}
		}

		return new ResponseParser(CR).parseResponse(responseBuffer
				.toByteArray());
	}

	private void waitForAvr() {
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {

		}
	}

	private String buildRequest(String value) {
		StringBuilder request = new StringBuilder();
		request.append(command).append(parameter);
		if (null != value) {
			request.append(value);
		}
		request.append(CR);
		return request.toString();
	}
}
