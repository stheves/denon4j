package de.theves.denon4j;

import static de.theves.denon4j.Commands.MUTE;
import static de.theves.denon4j.Commands.PW;
import static de.theves.denon4j.Commands.SELECT_INPUT;
import static de.theves.denon4j.Commands.SELECT_VIDEO;
import static de.theves.denon4j.Commands.VOL;
import static de.theves.denon4j.Parameters.DOWN;
import static de.theves.denon4j.Parameters.OFF;
import static de.theves.denon4j.Parameters.ON;
import static de.theves.denon4j.Parameters.STANDBY;
import static de.theves.denon4j.Parameters.STATUS;
import static de.theves.denon4j.Parameters.UP;

public class Avr1912 extends AbstractAvReceiver {

	public Avr1912(String hostname, int port) {
		this(hostname, port, 3000);
	}

	public Avr1912(String hostname, int port, int timeToWait) {
		super(hostname, port, timeToWait);
	}

	public Response powerOn() {
		Response res = send(PW, ON);
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
		return send(PW, STANDBY);
	}

	public boolean isPowerOn() {
		return send(PW, STATUS).getResponse().get(0)
				.equals(PW.toString() + ON.toString());
	}

	public Response mute() {
		return send(MUTE, ON);
	}

	public Response unmute() {
		return send(MUTE, OFF);
	}

	public boolean isMuted() {
		return send(MUTE, STATUS).equals(MUTE.toString());
	}

	public Response getVolume() {
		return send(VOL, STATUS);
	}

	public Response volumeUp() {
		return send(VOL, UP);
	}

	public Response volumeDown() {
		return send(VOL, DOWN);
	}

	public Response changeVolume(String value) {
		return send(VOL, value);
	}

	public Response getInputSource() {
		return send(SELECT_INPUT, STATUS);
	}

	public Response selectInputSource(Sources source) {
		return send(SELECT_INPUT, source.toString());
	}

	public Response selectVideoSource(Sources source) {
		return send(SELECT_VIDEO, source.toString());
	}

	public Response play(Playback playback) {
		return send(SELECT_INPUT, playback.toString());
	}

	public Response isSleepTimerSet() {
		return send(Commands.SLP, Parameters.STATUS);
	}

	public Response sleepTimer(String value) {
		return send(Commands.SLP, value);
	}

	public Response sleepTimerOff() {
		return send(Commands.SLP, OFF);
	}
}
