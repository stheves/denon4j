package de.theves.denon4j;

enum Commands {
	PW("PW"), MUTE("MU"), VOL("MV"), SELECT_INPUT("SI"), SELECT_VIDEO("SV"), SLP(
			"SLP");

	private String command;

	private Commands(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return this.command;
	}
}
