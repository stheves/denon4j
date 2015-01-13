package de.theves.denon4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Command {
	public static final char CR = 0x0d; // \r character

	private final String command;
	private final String parameter;

	public Command(String command, String parameter) {
		this.command = command;
		this.parameter = parameter;
	}

	public Response send(InetAddress address, int port) throws IOException {
		Socket socket = new Socket();
		socket.setSoTimeout(1000); // read timeout 1s
		try {
			// connect
			socket.connect(new InetSocketAddress(address, port), 5 * 1000);
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();

			// send the command
			String message = command + parameter + CR;
			out.write(message.getBytes());
			out.flush();

			// wait for AVR to send a response (see specification)
			waitForAvr();

			// receive the response
			byte[] buffer = new byte[512];
			if (in.available() != 0) {
				in.read(buffer);
				int end = 0;
				for (int i = 0; i < buffer.length; i++) {
					byte b = buffer[i];
					if (b == 0) {
						end = i;
						break;
					}
				}
				byte[] responseBuffer = new byte[end];
				System.arraycopy(buffer, 0, responseBuffer, 0, end);
				return new Response(responseBuffer);
			}
			throw new IllegalStateException();
		} finally {
			socket.close();
		}
	}

	private void waitForAvr() {
		try {
			Thread.sleep(200); // as of specification
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
