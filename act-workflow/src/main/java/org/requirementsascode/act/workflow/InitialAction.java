package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;

import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class InitialAction implements Transitionable<WorkflowState, Token> {
	private final Action initialAction;

	InitialAction(Action initialAction) {
		this.initialAction = requireNonNull(initialAction, "initialAction must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return dataFlow(new DefaultNode(owningStatemachine), initialAction)
			.asTransition(owningStatemachine);
	}
}
