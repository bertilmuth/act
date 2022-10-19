package org.requirementsascode.act.token;

class TriggerNextStep implements ActionData{
	private static final TriggerNextStep triggerNextStep = new TriggerNextStep();
	public static TriggerNextStep triggerNextStep() {
		return triggerNextStep;
	}
}