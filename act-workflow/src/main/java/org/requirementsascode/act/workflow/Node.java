package org.requirementsascode.act.workflow;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public interface Node {
	String name();

	State<WorkflowState, Token> asState();

	Data<WorkflowState, Token> storeToken(Data<WorkflowState, Token> inputDataWithToken);
}
