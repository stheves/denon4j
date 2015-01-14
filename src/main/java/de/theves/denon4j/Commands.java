package de.theves.denon4j;

public enum Commands {
	PWON("PWON"), PWOFF("PWSTANDBY"), PWSTATUS("PW?"), MUTE("MUON"), UNMUTE(
			"MUOFF"), MUTESTATUS("MU?"), VOLUP("MVUP"), VOLDOWN("MVDOWN"), VOLSTATUS(
			"MV?"), VOLSET("MV"), INPUTSET("SI"), INPUTSTATUS("SI?");

	private String command;

	private Commands(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return this.command;
	}
}
