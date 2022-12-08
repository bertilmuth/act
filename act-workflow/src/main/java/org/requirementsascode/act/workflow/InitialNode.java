package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;

import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class InitialNode implements Transitionable<WorkflowState, Token> {
	private final Action initialNode;

	InitialNode(Action initialNode) {
		this.initialNode = requireNonNull(initialNode, "initialNode must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return dataFlow(new AnyNode(), initialNode)
			.asTransition(owningStatemachine);
	}
}
