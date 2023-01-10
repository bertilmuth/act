package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class ActionNode<T extends ActionData, U extends ActionData> implements Node {
	private Port<T> inputPort;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	ActionNode(Port<T> inputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public String name() {
		return inputPort.name();
	}
	
	@Override
	public Class<? extends ActionData> type() {
		return inputPort.type();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(inputPort.name(), this::areTokensInNodesBefore,  
			inCase(this::isActionDataOfType, this::consumeToken));
	}
	
	private boolean areTokensInNodesBefore(WorkflowState state) {
		return state.areTokensInNodesBefore(this);
	}

	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		U outputActionData = applyActionFunction(inputData);
		Token inputToken = Token.from(inputData);
		Token outputToken = inputToken.replaceActionData(outputActionData);
		WorkflowState newState = inputData.state().updateActionOutput(outputActionData);
		return data(newState, outputToken);
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
		return "ActionNode[" + inputPort.name() + "]";
	}
}
