package org.requirementsascode.act.token;

class TriggerStep implements ActionData{
	private static final TriggerStep triggerStep = new TriggerStep();
	public static Token triggerStep() {
		return Token.token(null, triggerStep);
	}
}