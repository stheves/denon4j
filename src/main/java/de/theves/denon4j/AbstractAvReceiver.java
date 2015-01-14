package de.theves.denon4j;

import java.io.IOException;
import java.net.InetAddress;

public abstract class AbstractAvReceiver {

	protected final InetAddress avrAddress;
	protected final int port;

	public AbstractAvReceiver(InetAddress address, int port) {
		this.avrAddress = address;
		this.port = port;
	}

	protected Response send(Commands command) {
		return send(command, null);
	}

	protected Response send(Commands command, String value) {
		try {
			return new Command(command.toString())
					.send(avrAddress, port, value);
		} catch (IOException e) {
			throw new TransmitException(e);
		}
	}

}