package org.requirementsascode.act.token;

class TriggerSystemFunction implements ActionData{
	private static final TriggerSystemFunction triggerNextStep = new TriggerSystemFunction();
	public static TriggerSystemFunction triggerNextStep() {
		return triggerNextStep;
	}
}