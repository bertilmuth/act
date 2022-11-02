package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final ActionBehavior actionBehavior;
	
	private Action(String name, ActionBehavior actionBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
	}	
	
	public static Action action(String name, ActionBehavior actionBehavior) {
		return new Action(name, actionBehavior);
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

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
