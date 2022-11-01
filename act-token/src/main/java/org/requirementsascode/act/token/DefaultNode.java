package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class DefaultNode implements Node {
	private final State<Workflow, Token> defaultState;

	DefaultNode(Statemachine<Workflow, Token> statemachine) {
		requireNonNull(statemachine, "statemachine must be non-null!");
		this.defaultState = statemachine.defaultState();
	}

	@Override
	public String name() {
		return defaultState.name();
	}

	@Override
	public State<Workflow, Token> asState() {
		return defaultState;
	}
}