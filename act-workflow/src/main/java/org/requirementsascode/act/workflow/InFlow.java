package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Data;
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
		return transition(owningStatemachine.initialState(), inPort.asState(), this::addTokenToInPort);
	}
	
	private Data<WorkflowState, Token> addTokenToInPort(Data<WorkflowState, Token> data) {
		return data.state().addToken(inPort, Token.from(data));
	}
}
