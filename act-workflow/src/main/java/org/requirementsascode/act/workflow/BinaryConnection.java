package org.requirementsascode.act.workflow;

import static java.util.Collections.singletonList;

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
}
