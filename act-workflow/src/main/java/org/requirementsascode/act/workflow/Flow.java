package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class Flow implements Transitionable<WorkflowState, Token>{
	private final Part<?,?> owner;
	private Behavior<WorkflowState, Token, Token> behavior;
	
	Flow(Part<?,?> owner, Behavior<WorkflowState, Token, Token> behavior) {
		this.owner = requireNonNull(owner, "owner must be non-null!");
		this.behavior = requireNonNull(behavior, "behavior must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts().asOneState(), outPorts().asOneState(), behavior);
	}

	public Ports inPorts() {
		return owner.inPorts();
	}

	public Ports outPorts() {
		return owner.outPorts();
	}
}
