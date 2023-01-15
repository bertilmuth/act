package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Action<T extends ActionData, U extends ActionData> implements Named, ExecutableNode {
	private String name;
	private final Flow<T,U> flow;

	Action(String actionName, Port<T> inPort, Port<U> outPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");		
		this.flow = WorkflowApi.flow(inPort, outPort, actionFunction);
	}
	
	Action(String actionName, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");		
		this.flow = WorkflowApi.flow(inPorts, outPorts, actionFunction);
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Ports inPorts() {
		return flow.inPorts();
	}
	
	@Override
	public Ports outPorts() {
		return flow.outPorts();
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
