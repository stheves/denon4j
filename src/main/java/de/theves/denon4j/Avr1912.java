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

public class Avr1912 extends AbstractAvReceiver {

	public Avr1912(String hostname, int port) {
		this(hostname, port, 3000);
	}

	public Avr1912(String hostname, int port, int timeToWait) {
		super(hostname, port, timeToWait);
	}

	public Response powerOn() {
		Response res = send(PWON);
		// as specification - K) 1 seconds later, please
		// transmit the next COMMAND after transmitting a
		// power on COMMAND （ PWON ）
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// ignore
		}
		return res;
	}

	public Response powerOff() {
		return send(PWOFF);
	}

	public boolean isPowerOn() {
		return send(PWSTATUS).getResponse().get(0).equals(PWON.toString());
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

	public Response changeVolume(String value) {
		return send(VOLSET, value);
	}

	public Response getInputSource() {
		// TODO parse response and return InputSource
		return send(INPUTSTATUS);
	}

	public Response changeInputSource(InputSource input) {
		return send(INPUTSET, input.toString());
	}
}
