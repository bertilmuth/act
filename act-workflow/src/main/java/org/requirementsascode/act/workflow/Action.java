package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.ports;

import org.requirementsascode.act.workflow.behavior.PartBehavior;

public class Action implements Named, Part{
	private final String name;
	private final Ports inPorts;
	private final Ports outPorts;
	private final PartBehavior partBehavior;

	Action(String actionName, Port<?> inPort, Port<?> outPort, PartBehavior partBehavior) {
		this(actionName, ports(inPort), ports(outPort), partBehavior);
	}
	
	Action(String actionName, Ports inPorts, Ports outPorts, PartBehavior partBehavior) {
		this.name = requireNonNull(actionName, "actionName must be non-null!");	
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");	
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");	
		this.partBehavior = requireNonNull(partBehavior, "partBehavior must be non-null!");
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
		return new Flow(this, partBehavior);
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
