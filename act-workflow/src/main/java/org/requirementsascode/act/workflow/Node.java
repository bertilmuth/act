package org.requirementsascode.act.workflow;

import org.requirementsascode.act.statemachine.State;

public interface Node {
	String name();

	State<WorkflowState, Token> asState();
}
