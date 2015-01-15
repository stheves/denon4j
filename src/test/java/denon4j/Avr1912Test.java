package denon4j;

import org.junit.Test;

import de.theves.denon4j.Avr1912;
import de.theves.denon4j.InputSource;

public class Avr1912Test {

	private String address = "192.168.0.102";
	private int port = 23;

	@Test
	public void test() throws Exception {
		String vol = "505";

		Avr1912 avr = new Avr1912(address, port);
		avr.connect(1000);
		System.out.println("PWON: " + avr.isPowerOn());
		if (!avr.isPowerOn()) {
			System.out.println("POWERING ON: " + avr.powerOn());
		}
		System.out.println("MUTED: " + avr.isMuted());
		System.out.println("VOL: " + avr.getVolume());
		System.out.println("VOLUP: " + avr.volumeUp());
		System.out.println("VOLDOWN: " + avr.volumeDown());
		System.out.println("VOLSET: " + avr.changeVolume(vol));
		System.out.println("VOL: " + avr.getVolume());
		System.out.println("INPUT: " + avr.getInputSource());
		System.out.println("INPUTSET: "
				+ avr.changeInputSource(InputSource.SAT_CBL));
		System.out.println("INPUT: " + avr.getInputSource());
		avr.disconnect();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Starting demo...");
		Avr1912Test test = new Avr1912Test();
		test.test();
		System.out.println("Starting demo... Done.");
	}
}
