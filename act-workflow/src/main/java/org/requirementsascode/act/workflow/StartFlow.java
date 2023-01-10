package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class StartFlow implements Transitionable<WorkflowState, Token> {
	private final Node startNode;

	StartFlow(Node startNode) {
		this.startNode = requireNonNull(startNode, "startNode must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return dataFlow(new InitialNode(owningStatemachine), startNode).asTransition(owningStatemachine);
	}
}

class InitialNode implements Node{
	private Statemachine<WorkflowState, Token> owningStatemachine;

	public InitialNode(Statemachine<WorkflowState,Token> owningStatemachine) {
		this.owningStatemachine = owningStatemachine;
	}

	@Override
	public String name() {
		return owningStatemachine.initialState().name();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return owningStatemachine.initialState();
	}

	@Override
	public Class<? extends ActionData> type() {
		return ActionData.class;
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass());
	}
}
