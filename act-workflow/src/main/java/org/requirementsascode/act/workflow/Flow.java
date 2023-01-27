package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.workflow.behavior.PartBehavior;

public class Flow<T extends ActionData, U extends ActionData> implements Transitionable<WorkflowState, Token>{
	private final Part owner;
	private PartBehavior<T, U> partBehavior;
	
	Flow(PartBehavior<T, U> partBehavior) {
		this.partBehavior = requireNonNull(partBehavior, "partBehavior must be non-null!");
		this.owner = requireNonNull(partBehavior.owner(), "owner must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts().asOneState(), outPorts().asOneState(), partBehavior);
	}

	public Ports inPorts() {
		return owner.inPorts();
	}

	public Ports outPorts() {
		return owner.outPorts();
	}
}
