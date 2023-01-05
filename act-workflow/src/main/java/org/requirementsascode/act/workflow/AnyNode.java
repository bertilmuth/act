package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;

class AnyNode implements Node {
	AnyNode() {
	}

	@Override
	public String name() {
		return "Any Node";
	}

	@Override
	public State<WorkflowState, Token> asState() {
		State<WorkflowState, Token> state = state(name(), s -> true, Behavior.identity());
		return state;
	}
}