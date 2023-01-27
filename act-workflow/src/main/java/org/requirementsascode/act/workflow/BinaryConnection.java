package org.requirementsascode.act.workflow;

import java.util.Collections;

public class BinaryConnection<T extends ActionData> implements Part {
	private Port<T> inPort;
	private Port<? super T> outPort;

	public BinaryConnection(Port<T> inPort, Port<? super T> outPort) {
		this.inPort = inPort;
		this.outPort = outPort;
	}
	
	@Override
	public Ports inPorts() {
		return new Ports(Collections.singletonList(inPort));
	}

	@Override
	public Ports outPorts() {
		return new Ports(Collections.singletonList(outPort));
	}
}
