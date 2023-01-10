package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

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
		return state(actionName, this::areTokensInNodesBefore,  
			inCase(this::isActionDataOfType, this::consumeToken));
	}
	
	private boolean areTokensInNodesBefore(WorkflowState state) {
		return state.areTokensInNodesBefore(this);
	}

	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> outputData = transform(inputData);
		return outputData;
	}

	private Data<WorkflowState, Token> transform(Data<WorkflowState, Token> inputData) {
		Token inputToken = Token.from(inputData);
		U outputActionData = applyActionFunction(inputData);
		Token outputToken = inputToken.replaceActionData(outputActionData);
		WorkflowState newState = inputData.state().updateActionOutput(outputActionData);
		Data<WorkflowState, Token> outputData = data(newState, outputToken);
		return outputData;
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
