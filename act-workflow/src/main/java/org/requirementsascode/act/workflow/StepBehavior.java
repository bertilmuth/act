package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

class StepBehavior<T extends ActionData, U extends ActionData> implements Behavior<WorkflowState, Token, Token> {
	private final BiFunction<WorkflowState, T, U> functionOnActionData;
	private final Behavior<WorkflowState, ActionData, ActionData> typedFunction;

	StepBehavior(Class<T> inputClass, BiFunction<WorkflowState, T, U> functionOnActionData) {
		this.functionOnActionData = functionOnActionData;
		this.typedFunction = when(inputClass, this::applyFunctionOnActionData);
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, ActionData> functionInputData = unboxActionData(inputData);
		ActionData outputActionData = typedFunction.actOn(functionInputData).value().orElse(null);

		Token inputToken = tokenFrom(inputData);
		Token outputToken = inputToken.replaceActionData(outputActionData);

		Data<WorkflowState, Token> updatedWorkflow = inputData.state().replaceToken(inputToken, outputToken);
		return updatedWorkflow;
	}

	private Data<WorkflowState, ActionData> unboxActionData(Data<WorkflowState, Token> inputData) {
		return data(inputData.state(), ActionData.from(inputData));
	}

	private Token tokenFrom(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).orElseThrow(() -> new IllegalArgumentException("No token present!"));
	}

	private Data<WorkflowState, U> applyFunctionOnActionData(Data<WorkflowState, T> input) {
		WorkflowState workflowState = input.state();
		T inputActionData = input.value().orElse(null);
		U outputActionData = functionOnActionData.apply(workflowState, inputActionData);
		return data(workflowState, outputActionData);
	}
}