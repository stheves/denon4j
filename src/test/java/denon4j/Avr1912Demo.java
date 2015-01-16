package denon4j;

import de.theves.denon4j.Avr1912;
import de.theves.denon4j.Playback;
import de.theves.denon4j.Sources;

public class Avr1912Demo {

	public void demo(String host, int port) throws Exception {
		Avr1912 avr = new Avr1912(host, port);
		avr.connect(1000);
		System.out.println("PWON: " + avr.isPowerOn());
		if (!avr.isPowerOn()) {
			System.out.println("POWERING ON: " + avr.powerOn());
		}
		System.out.println("MUTED?: " + avr.isMuted());
		System.out.println("VOL?: " + avr.getVolume());
		System.out.println("VOLUP: " + avr.volumeUp());
		System.out.println("VOLDOWN: " + avr.volumeDown());
		System.out.println("VOLSET505: " + avr.changeVolume("55"));
		System.out.println("VOL?: " + avr.getVolume());
		System.out.println("INPUT: " + avr.getInputSource());
		System.out.println("INPUTSET: "
				+ avr.selectInputSource(Sources.SAT_CBL));
		Thread.sleep(2000);
		System.out.println("INPUT?: " + avr.getInputSource());
		System.out.println("PLAY IRADION: "
				+ avr.play(Playback.NET_USB_INTERNET_RADIO_P));
		System.out.println("SLEEPTIMER?: " + avr.isSleepTimerSet());
		System.out.println("SLEEPTIMERSET: " + avr.sleepTimer("010"));
		System.out.println("SLEEPTIMEROFF:" + avr.sleepTimerOff());
		avr.disconnect();
	}

	public static void main(String[] args) throws Exception {
		if (null == args || args.length != 2) {
			System.err.println("Try java -jar $/path/to/jar $host $port");
			System.exit(1);
		}
		System.out.println(String.format("Starting demo... Receiver: %s:%s",
				args[0], args[1]));
		Avr1912Demo test = new Avr1912Demo();
		test.demo(args[0], Integer.parseInt(args[1]));
		System.out.println("Demo done.");
	}
}
