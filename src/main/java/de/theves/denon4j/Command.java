package de.theves.denon4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class Command {
	private static final char CR = 0x0d; // \r character
	private static final Charset ENCODING = Charset.forName("US-ASCII");

	private final String command;
	private final String parameter;

	Command(String fullCommand) {
		this(fullCommand.substring(0, 2), fullCommand.substring(2,
				fullCommand.length()));
	}

	Command(String command, String parameter) {
		this.command = command;
		this.parameter = parameter;
	}

	/**
	 * Sends the command to the receiver and waits for a response (blocking).
	 * 
	 * @param in
	 *            the input stream to read the response (not <code>null</code>).
	 * @param out
	 *            the output stream to write the command string (not
	 *            <code>null</code>).
	 * @param value
	 *            the value of the command to send (can be <code>null</code>).
	 * @return the response from the receiver. Only <code>null</code> if the
	 *         receiver didn`t sent a response for the command within the
	 *         <code>timeToWait</code> period. This may happen if a
	 *         <code>value</code> wasn`t changed actually.
	 * @throws IOException
	 */
	Response send(InputStream in, OutputStream out, String value)
			throws IOException {
		// send the command
		String request = buildRequest(value);
		out.write(request.getBytes(ENCODING));
		out.flush();

		// receive the response
		return receiveResponse(in);
	}

	private Response receiveResponse(InputStream in) throws IOException {
		ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
		try {
			// read the first byte - blocks until response is available
			responseBuffer.write(in.read());
		} catch (IOException e) {
			// can happen if we do not get a response within the timeout
			// interval
			return null;
		}
		// read the stream as long as input is available (we never really
		// reach the end of the stream)
		while (in.available() > 0) {
			responseBuffer.write(in.read());
		}

		// convert to response
		List<String> lines = new ArrayList<>();
		byte[] byteArray = responseBuffer.toByteArray();
		StringBuilder line = new StringBuilder();
		for (byte b : byteArray) {
			if (CR == (char) b) {
				lines.add(line.toString());
				line.delete(0, line.length());
			} else {
				line.append((char) b);
			}
		}
		return new Response(lines);
	}

	private String buildRequest(String value) {
		StringBuilder request = new StringBuilder();
		request.append(command).append(parameter);
		if (value != null) {
			request.append(value);
		}
		request.append(CR);
		return request.toString();
	}
}
