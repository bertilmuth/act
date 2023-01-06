package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

class ActionBehavior<T extends ActionData, U extends ActionData> implements Behavior<WorkflowState, ActionData, ActionData> {
	private final BiFunction<WorkflowState, T, U> functionOnActionData;
	private final Behavior<WorkflowState, ActionData, ActionData> typedFunction;

	ActionBehavior(Class<T> inputClass, BiFunction<WorkflowState, T, U> functionOnActionData) {
		this.functionOnActionData = functionOnActionData;
		this.typedFunction = when(inputClass, this::applyFunctionOnActionData);
	}

	@Override
	public Data<WorkflowState, ActionData> actOn(Data<WorkflowState, ActionData> inputData) {
		return typedFunction.actOn(inputData);
	}

	private Data<WorkflowState, U> applyFunctionOnActionData(Data<WorkflowState, T> input) {
		WorkflowState workflowState = input.state();
		T inputActionData = input.value().orElse(null);
		U outputActionData = functionOnActionData.apply(workflowState, inputActionData);
		return data(workflowState, outputActionData);
	}
}