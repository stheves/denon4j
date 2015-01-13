package de.theves.denon4j;

import java.io.IOException;
import java.net.InetAddress;

public class Avr1912 {
	private static final Command PW_ON = new Command("PW", "ON");
	private static final Command PW_OFF = new Command("PW", "STANDBY");
	private static final Command PW_STATUS = new Command("PW", "?");

	private InetAddress avrAddress;
	private int port;

	public Avr1912(InetAddress address, int port) {
		this.avrAddress = address;
		this.port = port;
	}

	public Response powerOn() throws IOException {
		Response res = PW_ON.send(this.avrAddress, this.port);
		// as specification - K) 1 seconds later, please
		// transmit the next COMMAND after transmitting a
		// power on COMMAND （ PWON ）
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	public Response powerOff() throws IOException {
		return PW_OFF.send(this.avrAddress, this.port);
	}

	public boolean isOn() throws IOException {
		Response res = PW_STATUS.send(this.avrAddress, this.port);
		// TODO make this nicer and parse the status lines
		return res.getStatusLines().contains("PWON");
	}

	public static void main(String[] args) throws Exception {
		Avr1912 avr = new Avr1912(InetAddress.getByName("192.168.0.102"), 23);
		if (avr.isOn()) {
			System.out.println("POWERING OFF");
			System.out.println(avr.powerOff());
		} else {
			System.out.println("POWERING ON");
			System.out.println(avr.powerOn());
		}
	}
}
