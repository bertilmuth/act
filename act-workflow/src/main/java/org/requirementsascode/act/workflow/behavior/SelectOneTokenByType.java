package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.emptyToken;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

class SelectOneTokenByType<T> implements Behavior<WorkflowState, Token, Token> {
	private final Class<T> type;
	private final Part owner;

	public SelectOneTokenByType(Class<T> type, Part owner) {
		this.type = requireNonNull(type, "type must be non-null!");
		this.owner = requireNonNull(owner, "owner must be non-null!");
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		
		Ports inPorts = owner.inPorts();
		
		Token outToken = inPorts.stream()
			.flatMap(p -> p.tokens(state))
			.filter(t -> t.actionData().isPresent())
			.filter(t -> type.isAssignableFrom(t.actionData().get().getClass()))
			.findFirst()
			.orElse(emptyToken());
		return data(state, outToken);
	}

}
