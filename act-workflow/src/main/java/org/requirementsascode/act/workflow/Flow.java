package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class Flow<T extends ActionData, U extends ActionData> implements Transitionable<WorkflowState, Token>{
	private final Port<T> inputPort;
	private final Port<U> outputPort;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	Flow(Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inputPort.asState(), outputPort.asState(), this::consumeToken);
	}
	
	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> functionInputData = removeTokenFromInputPort(inputData);
		Data<WorkflowState, Token> functionOutputData = transform(functionInputData);
		Data<WorkflowState, Token> outputData = addTokenToOutputPort(functionOutputData);
		return outputData;
	}

	private Data<WorkflowState, Token> removeTokenFromInputPort(Data<WorkflowState, Token> data) {
		WorkflowState state = data.state();
		Token firstTokenInInputPort = state.firstTokenIn(inputPort).get();
		Data<WorkflowState, Token> updatedData = state.removeToken(inputPort, firstTokenInInputPort);
		return updatedData;
	}
	
	private Data<WorkflowState, Token> transform(Data<WorkflowState, Token> inputData) {
		Token inputToken = Token.from(inputData);
		Data<WorkflowState, Token> outputData = inputData;
				
		if(inputToken.actionData().isPresent()) {
			U outputActionData = applyActionFunction(inputData);
			Token outputToken = inputToken.replaceActionData(outputActionData);
			WorkflowState newState = inputData.state().updateActionOutput(outputActionData);
			outputData = data(newState, outputToken);
		} 
		return outputData;
	}
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Data<WorkflowState, Token> transformedData) {
		WorkflowState transformedState = transformedData.state();
		Token token = Token.from(transformedData);
		Data<WorkflowState, Token> outputPortData = transformedState.addToken(outputPort, token);
		return outputPortData;
	}

	@SuppressWarnings("unchecked")
	private U applyActionFunction(Data<WorkflowState, Token> inputData) {
		U outputActionData = actionFunction.apply(inputData.state(), (T) ActionData.from(inputData));
		return outputActionData;
	}
}
