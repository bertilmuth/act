package org.requirementsascode.act.workflow;

import java.util.Collections;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

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

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return null;
	}
}
