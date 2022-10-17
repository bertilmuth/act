package org.requirementsascode.act.token;
import static org.requirementsascode.act.token.Token.token;

class TriggerNextStep implements ActionData{
	private static final TriggerNextStep triggerNextStep = new TriggerNextStep();
	public static Token triggerNextStep() {
		return token(null, triggerNextStep);
	}
}