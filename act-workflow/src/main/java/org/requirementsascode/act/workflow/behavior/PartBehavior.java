package org.requirementsascode.act.workflow.behavior;

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

public class PartBehavior<T extends ActionData, U extends ActionData> implements Behavior<WorkflowState, Token, Token> {
	private final Part owner;
	private final Class<T> type;
	private final Ports inPorts;
	private final Ports outPorts;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	public PartBehavior(Part owner, Class<T> type, BiFunction<WorkflowState, T, U> actionFunction) {
		this.owner = requireNonNull(owner, "owner must be non-nulll!");
		this.inPorts = owner.inPorts();
		this.outPorts = owner.outPorts();
		this.type = requireNonNull(type, "type must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}
	
	public Part owner() {
		return owner;
	}
	
	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		return transformAndMove(inputData);	
	}
	
	private Data<WorkflowState, Token> transformAndMove(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> result = new SelectOneTokenByType<>(inPorts, type)
			.andThen(this::applyActionFunction)
			.andThen(new AddTokenToPorts(outPorts))
			.andThen(new RemoveFirstTokenFromPorts(inPorts))
			.actOn(inputData);
		return result;
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
