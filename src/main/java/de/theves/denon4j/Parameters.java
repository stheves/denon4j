package de.theves.denon4j;

public enum Parameters {
	ON("ON"), STANDBY("STANDBY"), OFF("STANDBY"), STATUS("?"), UP("UP"), DOWN(
			"DOWN");
	private String name;

	private Parameters(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	};
}
