package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.Collections;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class InFlow implements Transitionable<WorkflowState, Token> {
	private final Port<?> inPort;

	InFlow(Port<?> inPort) {
		this.inPort = requireNonNull(inPort, "inPort must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		Ports inPorts = new Ports(Collections.singletonList(inPort));
		State<WorkflowState, Token> inPortsState = inPorts.asState();
		return transition(owningStatemachine.initialState(), inPortsState, this::addTokenToInPort);
	}
	
	private Data<WorkflowState, Token> addTokenToInPort(Data<WorkflowState, Token> data) {
		return data.state().addToken(inPort, Token.from(data));
	}
}
