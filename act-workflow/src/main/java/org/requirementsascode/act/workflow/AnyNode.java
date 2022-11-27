package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;

import org.requirementsascode.act.core.Data;
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
	
	@Override
	public Data<WorkflowState, Token> consumeToken(WorkflowState state,
			Data<WorkflowState, Token> inputDataWithToken) {
		return state.moveToken(inputDataWithToken, this);
	}
}