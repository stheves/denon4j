package de.theves.denon4j;

public enum Sources {
	CD("CD"), TUNER("TUNER"), DVD("DVD"), BD("BD"), TV("TV"), SAT_CBL("SAT/CBL"), GAME(
			"GAME"), GAME2("GAME2"), AUX("V.AUX"), DOCK("DOCK"), SOURCE(
			"SOURCE"), IPOD("IPOD"), NET_UBS("NET/USB"), RHAPSODY("RHAPSODY"), NAPSTER(
			"NAPSTER"), PANDORA("PANDORA"), LASTFM("LASTFM"), FLICKR("FLICKR"), FAVORITES(
			"FAVORITES"), IRADIO("IRADIO"), UPNP_SERVER("SERVER"), USB_IPOD(
			"USB/IPOD");

	private String inputSource;

	private Sources(String source) {
		this.inputSource = source;
	}

	@Override
	public String toString() {
		return inputSource;
	}
}
