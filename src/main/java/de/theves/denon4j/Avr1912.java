package de.theves.denon4j;

import static de.theves.denon4j.Commands.INPUTSET;
import static de.theves.denon4j.Commands.INPUTSTATUS;
import static de.theves.denon4j.Commands.MUTE;
import static de.theves.denon4j.Commands.MUTESTATUS;
import static de.theves.denon4j.Commands.PWOFF;
import static de.theves.denon4j.Commands.PWON;
import static de.theves.denon4j.Commands.PWSTATUS;
import static de.theves.denon4j.Commands.UNMUTE;
import static de.theves.denon4j.Commands.VOLDOWN;
import static de.theves.denon4j.Commands.VOLSET;
import static de.theves.denon4j.Commands.VOLSTATUS;
import static de.theves.denon4j.Commands.VOLUP;

import java.io.IOException;
import java.net.InetAddress;

public class Avr1912 extends AbstractAvReceiver {
	public Avr1912(InetAddress address, int port) {
		super(address, port);
	}

	public Response powerOn() throws TransmitException {
		Command powerOn = new Command(PWON.toString());
		Response res;
		try {
			res = powerOn.send(this.avrAddress, this.port);

			// as specification - K) 1 seconds later, please
			// transmit the next COMMAND after transmitting a
			// power on COMMAND （ PWON ）
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
			return res;
		} catch (IOException e) {
			throw new TransmitException(e);
		}
	}

	public Response powerOff() throws TransmitException {
		return send(PWOFF);
	}

	public boolean isPowerOn() throws TransmitException {
		return send(PWSTATUS).getResponse().equals(PWON.toString());
	}

	public Response mute() {
		return send(MUTE);
	}

	public Response unmute() {
		return send(UNMUTE);
	}

	public boolean isMuted() {
		return send(MUTESTATUS).equals(MUTE.toString());
	}

	public Response getVolume() {
		return send(VOLSTATUS);
	}

	public Response volumeUp() {
		return send(VOLUP);
	}

	public Response volumeDown() {
		return send(VOLDOWN);
	}

	public void setVolume(String value) {
		send(VOLSET, value);
	}

	public InputSource getInputSource() {
		Response response = send(INPUTSTATUS);
		if (null != response) {
			String is = response.getResponse().substring(2)
					.replaceFirst("/", "_");
			return InputSource.valueOf(is);
		}
		return null;
	}

	public void setInputSource(InputSource input) {
		send(INPUTSET, input.toString());
	}
}
