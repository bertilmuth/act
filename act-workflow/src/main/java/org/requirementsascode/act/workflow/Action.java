package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.*;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Action<T extends ActionData, U extends ActionData> implements Named, Part {
	private final String name;
	private final Ports inPorts;
	private final Ports outPorts;
	
	private final Class<T> inputType;
	private BiFunction<WorkflowState, T, U> actionFunction;

	Action(String actionName, Class<T> inputType, Port<T> inPort, Port<U> outPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this(actionName, inputType, ports(inPort), ports(outPort), actionFunction);
	}
	
	Action(String actionName, Class<T> inputType, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");	
		this.inputType = requireNonNull(inputType, "inputType must be non-null!");	
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");	
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");	
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");	
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Ports inPorts() {
		return inPorts;
	}
	
	@Override
	public Ports outPorts() {
		return outPorts;
	}
	
	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return flow(inputType, inPorts(), outPorts(), actionFunction).asTransition(owningStatemachine);
	}

	@Override
	public String toString() {
		return name();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Named && 
			name().equals(((Named)obj).name());
	}
	
	@Override
	public int hashCode() {
		return name().hashCode();
	}
}
