package de.theves.denon4j;

public enum Playback {
	USB_P("USB"), NET_USB_IPOD_P("IPD"), NET_USB_INTERNET_RADIO_P("IRP"), NET_USB_FAVORITES_P(
			"FVP");
	private String playback;

	private Playback(String playback) {
		this.playback = playback;
	}

	@Override
	public String toString() {
		return playback;
	}
}
