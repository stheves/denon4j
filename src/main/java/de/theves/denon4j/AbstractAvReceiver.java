package de.theves.denon4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class AbstractAvReceiver {

	protected final String hostname;
	protected final int port;
	protected Socket socket;
	protected final int readTimeout;

	public AbstractAvReceiver(String hostname, int port, int readTimeout) {
		this.hostname = hostname;
		this.port = port;
		this.readTimeout = readTimeout;
	}

	public void connect(int timeout) throws ConnectException {
		if (isConnected()) {
			throw new IllegalStateException("Already connected.");
		}
		try {
			socket = new Socket();
			socket.setSoTimeout(readTimeout);
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
		socket = null;
	}

	public boolean isConnected() {
		return null != socket && socket.isConnected();
	}

	protected Response send(Commands command, String value)
			throws ConnectionException {
		return send(command, null, value);
	}

	protected Response send(Commands command, Parameters parameter)
			throws ConnectionException {
		return send(command, parameter, null);
	}

	protected Response send(Commands command, Parameters parameter, String value)
			throws ConnectionException {
		return doSend(command, parameter, value);
	}

	private Response doSend(Commands command, Parameters parameter, String value)
			throws ConnectionException {
		checkConnection();
		try {
			StringBuilder cmdBuilder = new StringBuilder();
			cmdBuilder.append(command.toString());
			if (parameter != null) {
				cmdBuilder.append(parameter.toString());
			}
			return new Command(cmdBuilder.toString()).send(socket, value);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}

	private void checkConnection() {
		if (!isConnected()) {
			throw new ConnectionException("Not connected.");
		}
	}
}