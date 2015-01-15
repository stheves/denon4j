package de.theves.denon4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class AbstractAvReceiver {

	protected final String hostname;
	protected final int port;
	protected Socket socket;
	protected final int timeToWait;

	public AbstractAvReceiver(String hostname, int port, int timeToWait) {
		this.hostname = hostname;
		this.port = port;
		this.timeToWait = timeToWait;
	}

	public void connect(int timeout) throws ConnectException {
		try {
			socket = new Socket();
			socket.setSoTimeout(timeToWait);
			socket.connect(new InetSocketAddress(hostname, port), timeout);
		} catch (IOException e) {
			throw new ConnectException(e);
		}
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			// ignore
		}
	}

	protected Response send(Commands command) throws ConnectionException {
		return send(command, null);
	}

	protected Response send(Commands command, String value)
			throws ConnectionException {
		return doSend(command, value);
	}

	private Response doSend(Commands command, String value)
			throws ConnectionException {
		if (!socket.isConnected()) {
			throw new ConnectionException("Not connected or connection lost.");
		}
		try {
			return new Command(command.toString()).send(
					socket.getInputStream(), socket.getOutputStream(), value);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}

}