package org.requirementsascode.act.token;

class TriggerStep implements ActionData{
	private static final TriggerStep runStep = new TriggerStep();
	public static TriggerStep triggerStep() {
		return runStep;
	}
}