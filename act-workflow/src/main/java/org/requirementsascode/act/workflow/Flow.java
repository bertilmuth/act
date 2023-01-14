package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class Flow<T extends ActionData, U extends ActionData> implements Transitionable<WorkflowState, Token>{
	private final Port<T> inputPort;
	private final Ports inputPorts;
	private final Port<U> outputPort;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	Flow(Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.inputPorts = new Ports(Collections.singletonList(inputPort));
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inputPort.asState(), outputPort.asState(), this::transformAndMove);
	}
	
	private Data<WorkflowState, Token> transformAndMove(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> functionInputData = removeTokenFromInputPorts(inputPorts, inputData);
		Data<WorkflowState, Token> functionOutputData = transform(functionInputData);
		Data<WorkflowState, Token> outputData = addTokenToOutputPort(outputPort, functionOutputData);
		return outputData;
	}
	
	private Data<WorkflowState, Token> removeTokenFromInputPorts(Ports inputPorts, Data<WorkflowState, Token> data) {
		List<Port<?>> ports = inputPorts.stream().collect(Collectors.toList());
		Data<WorkflowState, Token> updatedData = data;
		for (Port<?> port : ports) {
			updatedData = removeTokenFromInputPort(port, updatedData);
		}
		return updatedData;
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
