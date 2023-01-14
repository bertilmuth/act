package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class Action<T extends ActionData, U extends ActionData> implements Named, Transitionable<WorkflowState, Token> {
	private String name;
	private final Flow<T,U> flow;

	Action(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");
		this.flow = WorkflowApi.flow(inputPort, outputPort, actionFunction);
	}

	@Override
	public String name() {
		return name;
	}
	
	Flow<T,U> flow() {
		return flow;
	}
	
	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return flow.asTransition(owningStatemachine);
	}

	@Override
	public String toString() {
		return name();
	}
}
