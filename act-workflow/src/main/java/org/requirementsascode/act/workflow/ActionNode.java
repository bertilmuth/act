package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class ActionNode<T extends ActionData, U extends ActionData> implements Node {
	private final String name;
	private final Class<? extends ActionData> inputClass;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	ActionNode(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.inputClass = requireNonNull(inputClass, "inputClass must be non-null!");		
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Class<? extends ActionData> inputClass() {
		return inputClass;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(name(), this::areTokensInNodesBefore,  
			inCase(this::isActionDataOfInputClass, this::consumeToken));
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
	
	private boolean isActionDataOfInputClass(Data<WorkflowState,Token> inputData) {
		ActionData actionData = ActionData.from(inputData);
		return inputClass.isAssignableFrom(actionData.getClass());
	}

	@SuppressWarnings("unchecked")
	private U applyActionFunction(Data<WorkflowState, Token> inputData) {
		U outputActionData = actionFunction.apply(inputData.state(), (T) ActionData.from(inputData));
		return outputActionData;
	}

	@Override
	public String toString() {
		return "ActionNode[" + name + "]";
	}
}
