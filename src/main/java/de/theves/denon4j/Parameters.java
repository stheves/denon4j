package de.theves.denon4j;

enum Parameters {
	ON("ON"), STANDBY("STANDBY"), OFF("OFF"), STATUS("?"), UP("UP"), DOWN(
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
