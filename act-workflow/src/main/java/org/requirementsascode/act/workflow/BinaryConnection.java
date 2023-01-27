package org.requirementsascode.act.workflow;

import static java.util.Collections.singletonList;

import org.requirementsascode.act.workflow.behavior.PartBehavior;

public class BinaryConnection<T extends ActionData> implements Part<T,T> {
	private Port<T> inPort;
	private Port<? super T> outPort;

	public BinaryConnection(Port<T> inPort, Port<? super T> outPort) {
		this.inPort = inPort;
		this.outPort = outPort;
	}
	
	@Override
	public Ports inPorts() {
		return new Ports(singletonList(inPort));
	}

	@Override
	public Ports outPorts() {
		return new Ports(singletonList(outPort));
	}
	
	public Flow<T, T> asFlow() {
		PartBehavior<T,T> partBehavior = new PartBehavior<>(this, inPort.type(), this::transmitUnchanged);
		return new Flow<>(this, partBehavior);
	}
	
	private T transmitUnchanged(WorkflowState state, T actionData) {
		return actionData;
	}
}
