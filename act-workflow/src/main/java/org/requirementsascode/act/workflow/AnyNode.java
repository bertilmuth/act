package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.*;

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
	public Data<WorkflowState, Token> storeToken(WorkflowState workflowState, Token token){
		return data(workflowState,token);
	}
}