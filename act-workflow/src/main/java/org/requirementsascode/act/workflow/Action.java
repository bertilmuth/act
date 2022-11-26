package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final ActionBehavior actionBehavior;
	
	Action(String name, ActionBehavior actionBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
	}	
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public State<WorkflowState, Token> asState() {		
		State<WorkflowState, Token> state = state(name(), wf -> wf.tokens().isAnyTokenIn(name()), 
			when(Token.class, actionBehavior.asBehavior(this)));
		return state;
	}
	
	public Stream<Token> ownedTokens(WorkflowState workflowState){
		return workflowState.tokens().inNode(this.name);
	}

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
