package org.requirementsascode.act.core.testdata.trigger;

public class ConditionalTrigger_B1 implements Trigger {
	private final boolean triggering;

	public ConditionalTrigger_B1() {
		this(true);
	}

	public ConditionalTrigger_B1(boolean triggering) {
		this.triggering = triggering;
	}
	
	public boolean isTriggering() {
		return triggering;
	}
}