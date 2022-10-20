package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final Behavior<Workflow, ActionData, ActionData> behavior;
	
	private Action(String name, Behavior<Workflow, ActionData, ActionData> behavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.behavior = requireNonNull(behavior, "behavior must be non-null!");
	}
	
	public static Action action(String name, Behavior<Workflow, ActionData, ActionData> behavior) {
		return new Action(name, behavior);
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public Behavior<Workflow, ActionData, ActionData> behavior(){
		return behavior;
	}

	@Override
	public State<Workflow, Token> asState() {		
		State<Workflow, Token> state = state(name(), wf -> wf.tokens().isAnyTokenIn(name()), 
			when(Token.class, SystemFunction.systemFunction().asBehavior(this)));
		return state;
	}

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
