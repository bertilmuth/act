package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;

import org.requirementsascode.act.statemachine.State;

class AnyNode implements Node {
	AnyNode() {
	}

	@Override
	public String name() {
		return anyState().name();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return anyState();
	}
}