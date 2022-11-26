package org.requirementsascode.act.workflow;

import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public interface Node {
	String name();
	State<WorkflowState, Token> asState();
	
	default Stream<Token> tokens(WorkflowState workflowState){
		return workflowState.tokens().stream()
			.filter(token -> token.node().equals(this));
	}
}
