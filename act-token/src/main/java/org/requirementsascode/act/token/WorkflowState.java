package org.requirementsascode.act.token;

import java.util.Optional;

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

	public Optional<ActionData> actionOutput() {
		return Optional.ofNullable(actionOutput);
	}
}