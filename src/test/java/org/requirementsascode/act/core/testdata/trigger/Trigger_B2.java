package org.requirementsascode.act.core.testdata.trigger;

public class Trigger_B2 implements Trigger {
	private final Number number;

	public Trigger_B2(Number number) {
		this.number = number;
	}

	public Number number() {
		return number;
	}
}