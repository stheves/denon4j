package de.theves.denon4j;

public enum InputSource {
	CD("CD"), TUNER("TUNER"), DVD("DVD"), BD("BD"), TV("TV"), SAT_CBL("SAT/CBL"), GAME(
			"GAME"), GAME2("GAME2"), AUX("V.AUX"), DOCK("DOCK"), IPOD("IPOD"), NET_UBS(
			"NET/USB"), NAPSTER("NAPSTER"), LASTFM("LASTFM"), FAVORITES(
			"FAVORITES"), IRADIO("IRADIO"), UPNP_SERVER("SERVER"), USB_IPOD(
			"USB/IPOD"), USB_P("USB"), NET_USB_IPOD_P("IPD"), NET_USB_INTERNET_RADIO_P(
			"IRP"), NET_USB_FAVORITES_P("FVP");

	private String inputSource;

	private InputSource(String source) {
		this.inputSource = source;
	}

	@Override
	public String toString() {
		return inputSource;
	}
}
