package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Flow<T extends ActionData, U extends ActionData> implements ExecutableNode{
	private final Class<T> dataType;
	private final Ports inPorts;
	private final Ports outPorts;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	Flow(Class<T> dataType, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this.dataType = requireNonNull(dataType, "actionType must be non-null!");
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts.asOneState(), outPorts.asOneState(), 
			inCase(this::tokenHasRightType,this::transformAndMove));
	}
	
	private boolean tokenHasRightType(Data<WorkflowState, Token> inputData) {
		Optional<ActionData> firstInActionData = inPorts().firstActionData(inputData.state());
		Class<?> inputDataType = firstInActionData.map(ActionData::getClass)
			.orElseThrow(() -> new RuntimeException("Unexpected error: no token present in input ports of " + this + "!"));
		return dataType.isAssignableFrom(inputDataType);
	}
	
	@Override
	public Ports inPorts() {
		return inPorts;
	}
	
	@Override
	public Ports outPorts() {
		return outPorts;
	}
	
	private Data<WorkflowState, Token> transformAndMove(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> functionInputData = removeTokenFromInputPorts(inPorts, inputData);
		Data<WorkflowState, Token> functionOutputData = transform(functionInputData);
		Data<WorkflowState, Token> outputData = 
				addTokenToOutputPort(outPorts, functionOutputData);
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
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Ports outPorts, Data<WorkflowState, Token> data) {
		return data.state().addToken(outPorts.stream().findFirst().get(), Token.from(data));
	}

	@SuppressWarnings("unchecked")
	private U applyActionFunction(Data<WorkflowState, Token> inputData) {
		U outputActionData = actionFunction.apply(inputData.state(), (T) ActionData.from(inputData));
		return outputActionData;
	}
}
