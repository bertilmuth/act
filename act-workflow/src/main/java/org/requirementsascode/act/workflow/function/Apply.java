package org.requirementsascode.act.workflow.function;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.ActionData;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Ports;
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
		return transformAndMove(owner);	
	}
	
	private Behavior<WorkflowState, Token, Token> transformAndMove(Part owner) {
		Ports inPorts = owner.inPorts();
		Ports outPorts = owner.outPorts();
		
		return actOnSingleToken(inPorts)
			.andThen(new AddTokenToPorts(outPorts))
			.andThen(new RemoveFirstTokenFromPorts(inPorts));
	}

	private Behavior<WorkflowState, Token, Token> actOnSingleToken(Ports inPorts) {
		return new SelectOneTokenByType<>(inPorts, type)
			.andThen(this::applyActionFunction);
	}
	
	private Data<WorkflowState, Token> applyActionFunction(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Optional<Token> token = inputData.value();
		
		@SuppressWarnings("unchecked")
		Token outToken = token
			.flatMap(Token::actionData)
			.map(ad -> actionFunction.apply(state, (T)ad))
			.map(ad -> token.get().replaceActionData(ad))
			.orElse(Token.empty());
		
		return data(state, outToken);
	}
}
