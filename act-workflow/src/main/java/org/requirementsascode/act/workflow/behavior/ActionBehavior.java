package org.requirementsascode.act.workflow.behavior;

import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class ActionBehavior<T extends ActionData, U extends ActionData> implements Behavior<WorkflowState, Token, Token> {
	private final Class<T> type;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	public ActionBehavior(Class<T> type, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this.type = requireNonNull(type, "type must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}
	
	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		return applyActionFunction(inputData);	
	}

	private Data<WorkflowState, Token> applyActionFunction(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Optional<Token> token = inputData.value();
		
		@SuppressWarnings("unchecked")
		Token outToken = token
			.flatMap(Token::actionData)
			.map(ad -> actionFunction.apply(state, (T)ad))
			.map(ad -> token.get().replaceActionData(ad))
			.orElse(null);
		
		return data(state, outToken);
	}
}
