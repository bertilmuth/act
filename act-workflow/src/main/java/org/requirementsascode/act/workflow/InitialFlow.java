package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;

import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.workflow.trigger.StartWorkflow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class InitialFlow implements Transitionable<WorkflowState, Token> {
	private final Node initialNode;

	InitialFlow(Node initialNode) {
		this.initialNode = requireNonNull(initialNode, "initialNode must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return dataFlow(new StartNode<>(owningStatemachine), initialNode, StartWorkflow.class).asTransition(owningStatemachine);
	}
}

class StartNode<T> implements Node{
	private Statemachine<WorkflowState, Token> sm;

	public StartNode(Statemachine<WorkflowState,Token> sm) {
		this.sm = sm;
	}

	@Override
	public String name() {
		return "Start Node";
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return sm.initialState();
	}
	
}
