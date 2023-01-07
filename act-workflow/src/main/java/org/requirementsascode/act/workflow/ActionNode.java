package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class ActionNode implements Node {
	private final String name;
	private final Behavior<WorkflowState, ActionData, ActionData> actionBehavior;

	<T extends ActionData, U extends ActionData> ActionNode(String name, Class<T> inputClass, BiFunction<WorkflowState, T, U> actionFunction) {
		this.name = requireNonNull(name, "name must be non-null!");
		requireNonNull(actionFunction, "actionFunction must be non-null!");
		this.actionBehavior = when(inputClass, behaviorOf(actionFunction));
	}
	

	@Override
	public String name() {
		return name;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(name(), s -> s.areTokensIn(this), this::consumeToken);
	}

	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, ActionData> behaviorInputData = unboxActionData(inputData);
		ActionData outputActionData = actionBehavior.actOn(behaviorInputData).value().orElse(null);

		Token inputToken = Token.from(inputData);
		Token outputToken = inputToken.replaceActionData(outputActionData);

		Data<WorkflowState, Token> updatedWorkflow = inputData.state().replaceToken(this, inputToken, outputToken);
		return updatedWorkflow;
	}

	@Override
	public String toString() {
		return "ActionNode[" + name + "]";
	}
	
	private <T extends ActionData, U extends ActionData> Behavior<WorkflowState, T, U> behaviorOf(BiFunction<WorkflowState, T, U> actionFunction) {
		return d -> {
			WorkflowState state = d.state();
			U functionResult = actionFunction.apply(state, d.value().orElse(null));
			return data(state, functionResult);
		};
	}
	
	private Data<WorkflowState, ActionData> unboxActionData(Data<WorkflowState, Token> inputData) {
		return data(inputData.state(), ActionData.from(inputData));
	}
}
