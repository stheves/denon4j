package de.theves.denon4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

final class Command {
	private static final char CR = 0x0d; // \r character
	private static final Charset ENCODING = Charset.forName("US-ASCII");

	private final String command;
	private final String parameter;
	private final int timeToWait;

	Command(String fullCommand) {
		this(fullCommand.substring(0, 2), fullCommand.substring(2,
				fullCommand.length()));
	}

	Command(String command, String parameter) {
		this(command, parameter, 1000);
	}

	Command(String command, String parameter, int timeToWait) {
		this.command = command;
		this.parameter = parameter;
		this.timeToWait = timeToWait;
	}

	Response send(InetAddress address, int port) throws IOException {
		return send(address, port, null);
	}

	/**
	 * Sends the command to the receiver and waits for a response (blocking).
	 * 
	 * @param address
	 *            the inet address of the receiver (not <code>null</code>).
	 * @param port
	 *            the port (usually 23).
	 * @param value
	 *            the value of the command to send (can be <code>null</code>).
	 * @return the response from the receiver. Only <code>null</code> if the
	 *         command was a set command (value != null).
	 * @throws IOException
	 */
	Response send(InetAddress address, int port, String value)
			throws IOException {
		Socket socket = new Socket();
		socket.setSoTimeout(timeToWait);
		try {
			// connect
			socket.connect(new InetSocketAddress(address, port), 5 * 1000);
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();

			// send the command
			StringBuilder request = new StringBuilder();
			request.append(command).append(parameter);
			if (value != null) {
				request.append(value);
			}
			request.append(CR);
			out.write(request.toString().getBytes(ENCODING));
			out.flush();

			if (value == null) {
				// receive the response
				int n = 0;
				ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream(
						64);
				while (-1 != (n = in.read())) {
					// CR marks the end of the response
					if (CR == (char) n) {
						break;
					}
					responseBuffer.write(n);
				}
				return new Response(new String(responseBuffer.toByteArray(),
						ENCODING));
			}
			return null;
		} finally {
			socket.close();
		}
	}
}
