package de.theves.denon4j;

public enum Playback {
	USB("USB"), IPOD("IPD"), INTERNET_RADIO("IRP"), FAVORITES(
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
