package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.flow;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class Action<T extends ActionData, U extends ActionData> implements Node, Transitionable<WorkflowState, Token> {
	private String name;
	private final Flow<T,U> actionFlow;

	Action(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");
		this.actionFlow = flow(inputPort, outputPort, actionFunction);
	}

	@Override
	public String name() {
		return name;
	}
	
	Flow<T,U> actionFlow() {
		return actionFlow;
	}
	
	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return actionFlow.asTransition(owningStatemachine);
	}

	@Override
	public String toString() {
		return name();
	}
}
