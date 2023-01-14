package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Flow<T extends ActionData, U extends ActionData> implements ExecutableNode{
	private final Ports inPorts;
	private final Port<U> outPort;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	Flow(Ports inPorts, Port<U> outPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");
		this.outPort = requireNonNull(outPort, "outPort must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts.asState(), outPort.asState(), this::transformAndMove);
	}
	
	@Override
	public Ports inPorts() {
		return inPorts;
	}
	
	private Data<WorkflowState, Token> transformAndMove(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> functionInputData = removeTokenFromInputPorts(inPorts, inputData);
		Data<WorkflowState, Token> functionOutputData = transform(functionInputData);
		Data<WorkflowState, Token> outputData = addTokenToOutputPort(outPort, functionOutputData);
		return outputData;
	}
	
	private Data<WorkflowState, Token> removeTokenFromInputPorts(Ports inputPorts, Data<WorkflowState, Token> data) {
	    return inputPorts.stream()
	        .reduce(data, 
	        	(updatedData, port) -> removeTokenFromInputPort(port, updatedData), 
	        	(data1, data2) -> data2);
	}

	private Data<WorkflowState, Token> removeTokenFromInputPort(Port<?> inputPort, Data<WorkflowState, Token> data) {
		WorkflowState state = data.state();
		Token inputToken = state.firstTokenIn(inputPort).get();
		WorkflowState updatedState = state.removeToken(inputPort, inputToken).state();
		return data(updatedState, Token.from(data));
	}
	
	private Data<WorkflowState, Token> transform(Data<WorkflowState, Token> data) {
		U outputActionData = applyActionFunction(data);
		Token outputToken = Token.from(data).replaceActionData(outputActionData);
		return data(data.state(), outputToken);
	}
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Port<U> outputPort, Data<WorkflowState, Token> data) {
		return data.state().addToken(outputPort, Token.from(data));
	}

	@SuppressWarnings("unchecked")
	private U applyActionFunction(Data<WorkflowState, Token> inputData) {
		U outputActionData = actionFunction.apply(inputData.state(), (T) ActionData.from(inputData));
		return outputActionData;
	}
}
