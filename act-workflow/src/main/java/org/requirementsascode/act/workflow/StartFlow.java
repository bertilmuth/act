package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class StartFlow implements Transitionable<WorkflowState, Token> {
	private final Port<?> startPort;

	StartFlow(Port<?> startPort) {
		this.startPort = requireNonNull(startPort, "startPort must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(owningStatemachine.initialState(), startPort.asState(), this::addTokenToStartNode);
	}
	
	private Data<WorkflowState, Token> addTokenToStartNode(Data<WorkflowState, Token> data) {
		WorkflowState state = data.state();
		Token token = Token.from(data);
		Data<WorkflowState, Token> portData = state.addToken(startPort, token);
		return portData;
	}
}

class InitialNode implements Node{
	private Statemachine<WorkflowState, Token> owningStatemachine;

	public InitialNode(Statemachine<WorkflowState,Token> owningStatemachine) {
		this.owningStatemachine = owningStatemachine;
	}

	@Override
	public String name() {
		return owningStatemachine.initialState().name();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return owningStatemachine.initialState();
	}

	@Override
	public Class<? extends ActionData> type() {
		return ActionData.class;
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass());
	}
}
