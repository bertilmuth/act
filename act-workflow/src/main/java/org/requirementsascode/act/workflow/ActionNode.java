package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class ActionNode<T extends ActionData, U extends ActionData> implements Node {
	private String actionName;
	private final Port<T> inputPort;
	private final Port<U> outputPort;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	ActionNode(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.actionName = requireNonNull(actionName, "actionName must be non-null!");
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public String name() {
		return actionName;
	}
	
	@Override
	public Class<? extends ActionData> type() {
		return inputPort.type();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(actionName, this::areTokensInInputOrOutputPort,
			inCase(this::isActionDataOfType, d -> {
				Statemachine<WorkflowState, Token> sm = d.state().statemachine();
				Transition<WorkflowState, Token> transition = new DataFlow<>(inputPort, outputPort, actionFunction).asTransition(sm);
				Data<WorkflowState, Token> result = transition.asBehavior(sm).actOn(d);
				return result;
			}));
	}
	
	private boolean areTokensInInputOrOutputPort(WorkflowState state) {
		return state.areTokensIn(inputPort) || state.areTokensIn(outputPort);
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
		U outputActionData = applyActionFunction(inputData);
		Token outputToken = inputToken.replaceActionData(outputActionData);
		WorkflowState newState = inputData.state().updateActionOutput(outputActionData);
		Data<WorkflowState, Token> outputData = data(newState, outputToken);
		return outputData;
	}
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Data<WorkflowState, Token> transformedData) {
		WorkflowState transformedState = transformedData.state();
		Token token = Token.from(transformedData);
		Data<WorkflowState, Token> outputPortData = transformedState.addToken(outputPort, token);
		return outputPortData;
	}
	
	private boolean isActionDataOfType(Data<WorkflowState,Token> inputData) {
		ActionData actionData = ActionData.from(inputData);
		return inputPort.type().isAssignableFrom(actionData.getClass());
	}

	@SuppressWarnings("unchecked")
	private U applyActionFunction(Data<WorkflowState, Token> inputData) {
		U outputActionData = actionFunction.apply(inputData.state(), (T) ActionData.from(inputData));
		return outputActionData;
	}

	@Override
	public String toString() {
		return "ActionNode[" + name() + "]";
	}
}
