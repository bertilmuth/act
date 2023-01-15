package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.Collections;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class InFlow implements Transitionable<WorkflowState, Token> {
	private Port<?> inPort;
	private final State<WorkflowState, Token> state;

	InFlow(Port<?> inPort) {
		this.inPort = requireNonNull(inPort, "inPort must be non-null!");
		this.state = new Ports(Collections.singletonList(inPort)).asOneState();
	}
	
	State<WorkflowState, Token> asState(){
		return state;
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(anyState(), state, this::addTokenToInPort);
	}
	
	private Data<WorkflowState, Token> addTokenToInPort(Data<WorkflowState, Token> data) {
		return data.state().addToken(inPort, Token.from(data));
	}
}
