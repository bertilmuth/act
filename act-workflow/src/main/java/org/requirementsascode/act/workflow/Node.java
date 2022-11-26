package org.requirementsascode.act.workflow;

import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public interface Node {
	String name();
	State<WorkflowState, Token> asState();
	
	default boolean hasTokens(WorkflowState workflowState){
		return ownedTokens(workflowState).findAny().isPresent();
	}
	
	default Stream<Token> ownedTokens(WorkflowState workflowState){
		return workflowState.tokens().inNode(this);
	}
}
