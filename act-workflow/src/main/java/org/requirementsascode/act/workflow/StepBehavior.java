package org.requirementsascode.act.workflow;

import java.util.function.BiFunction;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;
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
		Data<WorkflowState, ActionData> inputActionData = unboxActionData(inputData);
		Data<WorkflowState, ActionData> outputActionData = typedFunction.actOn(inputActionData);

		Token inputToken = tokenFrom(inputData);
		Token outputToken = updateActionData(inputToken, outputActionData);

		Data<WorkflowState, Token> updatedWorkflow = inputData.state().replaceToken(inputToken, outputToken);
		return updatedWorkflow;
	}

	private Data<WorkflowState, ActionData> unboxActionData(Data<WorkflowState, Token> inputData) {
		return data(inputData.state(), ActionData.from(inputData));
	}

	private Token tokenFrom(Data<WorkflowState, Token> inputData) {
		return Token.from(inputData).orElseThrow(() -> new IllegalArgumentException("No token present!"));
	}

	private Token updateActionData(Token token, Data<WorkflowState, ActionData> outputActionData) {
		return token(token.node(), outputActionData.value().orElse(null));
	}

	private Data<WorkflowState, U> applyFunctionOnActionData(Data<WorkflowState, T> input) {
		WorkflowState workflowState = input.state();
		T inputActionData = input.value().orElse(null);
		U outputActionData = functionOnActionData.apply(workflowState, inputActionData);
		return data(workflowState, outputActionData);
	}
}