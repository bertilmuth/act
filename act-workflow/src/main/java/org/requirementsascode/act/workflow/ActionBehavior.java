package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class ActionBehavior<T extends ActionData, U extends ActionData> implements Behavior<WorkflowState, Token, Token> {
	private final BiFunction<WorkflowState, T, U> actionFunction;

	public ActionBehavior(BiFunction<WorkflowState, T, U> actionFunction) {
		this.actionFunction = Objects.requireNonNull(actionFunction, "actionFunction must be non-null!");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Optional<Token> token = inputData.value();
		
		U outActionData = token
			.flatMap(Token::actionData)
			.map(ad -> applyActionFunction(state, (T)ad))
			.orElse(null);
		
		return data(state, token(outActionData));	
	}
	
	private U applyActionFunction(WorkflowState state, T actionData) {
		return actionFunction.apply(state, actionData);
	}
}
