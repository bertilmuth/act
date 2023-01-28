package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.ports;

import java.util.function.BiFunction;

import org.requirementsascode.act.workflow.function.Apply;

public class Action<T extends ActionData, U extends ActionData> implements Named, Part{
	private final String name;
	private final Ports inPorts;
	private final Ports outPorts;
	private final Apply<T,U> apply;

	Action(String actionName, Port<T> inPort, Port<U> outPort, Class<T> inputType, BiFunction<WorkflowState, T, U> actionFunction) {
		this(actionName, ports(inPort), ports(outPort), inputType, actionFunction);
	}
	
	Action(String actionName, Ports inPorts, Ports outPorts, Class<T> inputType, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");	
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");	
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");	
		
		requireNonNull(actionFunction, "actionFunction must be non-null!");
		this.apply = new Apply<>(this, inputType, actionFunction);	
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
	public Flow asFlow() {
		return new Flow(this, apply);
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
