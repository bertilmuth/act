package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.workflow.behavior.PartBehavior;

public class Flow implements Transitionable<WorkflowState, Token>{
	private final Part owner;
	private PartBehavior partBehavior;
	
	Flow(Part owner, PartBehavior partBehavior) {
		this.owner = requireNonNull(owner, "owner must be non-null!");
		this.partBehavior = requireNonNull(partBehavior, "partBehavior must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return flow(inPorts().asOneState(), outPorts().asOneState(), partBehavior.asBehavior(owner))
			.asTransition(owningStatemachine);
	}

	public Ports inPorts() {
		return owner.inPorts();
	}

	public Ports outPorts() {
		return owner.outPorts();
	}
}
