package org.requirementsascode.act.token;

public class WorkflowState {
	private final Tokens tokens;
	private final ActionData actionOutput;
	
	public WorkflowState(Tokens tokens, ActionData actionOutput) {
		this.tokens = tokens;
		this.actionOutput = actionOutput;
	}

	public Tokens tokens() {
		return tokens;
	}

	public ActionData actionOutput() {
		return actionOutput;
	}
}