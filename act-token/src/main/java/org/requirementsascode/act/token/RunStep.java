package org.requirementsascode.act.token;

class RunStep implements ActionData{
	private static final RunStep runStep = new RunStep();
	public static RunStep runStep() {
		return runStep;
	}
}