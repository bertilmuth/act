package org.requirementsascode.act.token.function;

import org.requirementsascode.act.token.ActionData;

public class StepTrigger implements ActionData{
	private static final StepTrigger stepTrigger = new StepTrigger();
	public static StepTrigger stepTrigger() {
		return stepTrigger;
	}
}