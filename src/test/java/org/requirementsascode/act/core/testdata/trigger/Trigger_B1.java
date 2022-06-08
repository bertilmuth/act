package org.requirementsascode.act.core.testdata.trigger;

public class Trigger_B1 implements Trigger {
	private final Number number;

	public Trigger_B1(Number number) {
		this.number = number;
	}

	public Number getNumber() {
		return number;
	}
}