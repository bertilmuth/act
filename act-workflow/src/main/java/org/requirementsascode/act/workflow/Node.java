package org.requirementsascode.act.workflow;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public interface Node {
	String name();

	State<WorkflowState, Token> asState();

	default Data<WorkflowState, Token> moveTokenToMe(WorkflowState workflowState, Token tokenToMove) {
		return workflowState.moveToken(tokenToMove, this);
	}
}
