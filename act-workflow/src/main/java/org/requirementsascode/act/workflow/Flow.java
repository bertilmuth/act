package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Flow<T extends ActionData, U extends ActionData> implements ExecutableNode{
	private final Class<T> actionDataType;
	private final Ports inPorts;
	private final Ports outPorts;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	Flow(Class<T> actionDataType, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this.actionDataType = requireNonNull(actionDataType, "actionDataType must be non-null!");
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}
	
	@Override
	public Ports inPorts() {
		return inPorts;
	}
	
	@Override
	public Ports outPorts() {
		return outPorts;
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts.asOneState(), outPorts.asOneState(), 
			inCase(this::tokenHasRightType,this::transformAndMove));
	}
	
	private boolean tokenHasRightType(Data<WorkflowState, Token> inData) {
		Class<?> inActionDataType = firstInActionDataType(inData);
		return actionDataType.isAssignableFrom(inActionDataType);
	}

	private ActionData firstInActionData(Data<WorkflowState, Token> inData) {
		return inPorts().firstActionData(inData.state())
			.orElseThrow(() -> new RuntimeException("Unexpected error: no action data present in input ports of " + this + "!"));
	}
	
	private Class<? extends ActionData> firstInActionDataType(Data<WorkflowState, Token> inData) {
		return firstInActionData(inData).getClass();
	}
	
	private Data<WorkflowState, Token> transformAndMove(Data<WorkflowState, Token> inputData) {
		WorkflowState stateAfterRemoval = removeTokenFromInputPorts(inPorts(), inputData.state());
		Data<WorkflowState, Token> functionOutputData = transform(stateAfterRemoval, firstInActionData(inputData));
		Data<WorkflowState, Token> outputData = addTokenToOutputPort(outPorts, functionOutputData);
		return outputData;
	}
	
	private WorkflowState removeTokenFromInputPorts(Ports inputPorts, WorkflowState state) {
	    return inputPorts.stream()
	        .reduce(state, 
	        	(updatedState, port) -> removeTokenFromInputPort(port, updatedState), 
	        	(data1, data2) -> data2);
	}

	private WorkflowState removeTokenFromInputPort(Port<?> inputPort, WorkflowState state) {
		Token inputToken = state.firstTokenIn(inputPort).get();
		WorkflowState updatedState = state.removeToken(inputPort, inputToken).state();
		return updatedState;
	}
	
	@SuppressWarnings("unchecked")
	private Data<WorkflowState, Token> transform(WorkflowState state, ActionData firstInActionData) {
		U outActionData = applyActionFunction(state, (T)firstInActionData);
		return data(state, token(outActionData));
	}
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Ports outPorts, Data<WorkflowState, Token> data) {
		return data.state().addToken(outPorts.stream().findFirst().get(), Token.from(data));
	}

	private U applyActionFunction(WorkflowState state, T actionData) {
		return actionFunction.apply(state, actionData);
	}
}
