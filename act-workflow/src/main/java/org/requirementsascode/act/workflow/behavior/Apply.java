package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class Apply<T extends ActionData, U extends ActionData> implements PartBehavior {
	private final Class<T> type;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	public Apply(Class<T> type, BiFunction<WorkflowState, T, U> actionFunction) {
		this.type = requireNonNull(type, "type must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}
	
	@Override
	public Behavior<WorkflowState, Token, Token> asBehavior(Part owner) {
		return d -> {
			return new SelectOneTokenByType<>(type, owner)
				.andThen(this::applyActionFunction)
				.andThen(new AddTokenToOutPorts(owner))
				.andThen(new RemoveFirstTokenFromInPorts(owner))
				.actOn(d);
		};
	}
	
	@SuppressWarnings("unchecked")
	private Data<WorkflowState, Token> applyActionFunction(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Token token = Token.from(inputData);
		
		Token outToken = token.actionData()
			.map(ad -> actionFunction.apply(state, (T)ad))
			.map(ad -> token.replaceActionData(ad))
			.orElse(Token.empty());
		
		return data(state, outToken);
	}
}
