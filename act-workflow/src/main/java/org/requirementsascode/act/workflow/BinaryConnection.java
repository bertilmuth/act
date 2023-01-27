package org.requirementsascode.act.workflow;

import static java.util.Collections.singletonList;
import static org.requirementsascode.act.workflow.WorkflowApi.flow;

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
		return flow(this, inPort.type(), (s,ad) -> ad);
	}
}
