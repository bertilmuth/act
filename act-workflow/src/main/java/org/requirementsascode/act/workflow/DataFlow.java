package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class DataFlow<T extends ActionData, U extends ActionData> implements Transitionable<WorkflowState, Token>{
	private final Port<T> inputPort;
	private final Port<U> outputPort;

	DataFlow(Port<T> inputPort, Port<U> outputPort) {
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inputPort.asState(), outputPort.asState(), this::moveToken);
	}
	
	private Data<WorkflowState, Token> moveToken(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> transitionInputData = removeTokenFromInputPort(inputData);
		Data<WorkflowState, Token> transitionOutputData = addTokenToOutputPort(transitionInputData);
		return transitionOutputData;
	}

	private Data<WorkflowState, Token> removeTokenFromInputPort(Data<WorkflowState, Token> data) {
		WorkflowState state = data.state();
		Token firstTokenInInputPort = state.firstTokenIn(inputPort).get();
		Data<WorkflowState, Token> updatedData = state.removeToken(inputPort, firstTokenInInputPort);
		return updatedData;
	}
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Data<WorkflowState, Token> transformedData) {
		WorkflowState transformedState = transformedData.state();
		Token token = Token.from(transformedData);
		Data<WorkflowState, Token> outputPortData = transformedState.addToken(outputPort, token);
		return outputPortData;
	}
}
