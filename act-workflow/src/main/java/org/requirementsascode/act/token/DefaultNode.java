package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class DefaultNode implements Node {
	private final State<WorkflowState, Token> defaultState;

	DefaultNode(Statemachine<WorkflowState, Token> statemachine) {
		requireNonNull(statemachine, "statemachine must be non-null!");
		this.defaultState = statemachine.defaultState();
	}

	@Override
	public String name() {
		return defaultState.name();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return defaultState;
	}
}