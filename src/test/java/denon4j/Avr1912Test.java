package denon4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import de.theves.denon4j.Avr1912;
import de.theves.denon4j.InputSource;

public class Avr1912Test {

	private InetAddress address;
	private int port;

	@Before
	public void before() throws Exception {
		address = InetAddress.getByName("192.168.0.102");
		port = 23;
	}

	@Test
	public void test() throws Exception {
		// Thread listenerThread = new Thread(new Listener());
		// listenerThread.start();
		String vol = "505";

		Avr1912 avr = new Avr1912(address, port);
		System.out.println("PWON: " + avr.isPowerOn());
		if (!avr.isPowerOn()) {
			System.out.println("POWERING ON: " + avr.powerOn());
		}
		System.out.println("MUTED: " + avr.isMuted());
		System.out.println("VOL: " + avr.getVolume());
		System.out.println("VOLUP: " + avr.volumeUp());
		System.out.println("VOLDOWN: " + avr.volumeDown());
		System.out.println("VOLSET: " + vol);
		avr.setVolume(vol);
		System.out.println("VOL: " + avr.getVolume());
		System.out.println("INPUT: " + avr.getInputSource());
		InputSource inputSource = InputSource.GAME2;
		System.out.println("INPUTSET: " + inputSource);
		avr.setInputSource(inputSource);
		System.out.println("INPUT: " + avr.getInputSource());
		System.out.println("INPUT: " + avr.getInputSource());
	}

	public static void main(String[] args) throws Exception {
		Avr1912Test test = new Avr1912Test();
		test.before();
		test.test();
	}

	class Listener implements Runnable {

		@Override
		public void run() {
			Socket socket = new Socket();
			try {
				socket.setSoTimeout(10000); // read timeout 1s
				// connect
				socket.connect(new InetSocketAddress(address, port), 5 * 1000);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				// receive the response
				while (true) {
					System.out.println(reader.readLine());
					Thread.sleep(100);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// crap api
					e.printStackTrace();
				}
			}
		}

	}

}
