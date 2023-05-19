package org.requirementsascode.act.workflow;

import static java.util.Collections.singletonList;

import org.requirementsascode.act.workflow.behavior.Apply;

public class BinaryConnection<T extends ActionData> implements Part {
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
	
	@Override
	public TokenFlow asFlow() {
		Apply<T,T> partBehavior = new Apply<>(inPort.type(), this::transmitUnchanged);
		return new TokenFlow(this, partBehavior);
	}
	
	private T transmitUnchanged(WorkflowState state, T actionData) {
		return actionData;
	}
}
