package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;
import static org.requirementsascode.act.workflow.WorkflowApi.*;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class SelectOneTokenByType<T> implements Behavior<WorkflowState, Token, Token> {
	private final Ports ports;
	private final Class<T> type;

	public SelectOneTokenByType(Ports ports, Class<T> type) {
		this.ports = requireNonNull(ports, "ports must be non-null!");
		this.type = requireNonNull(type, "type must be non-null!");

	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Token outToken = ports.stream()
			.flatMap(p -> p.tokens(state))
			.filter(t -> t.actionData().isPresent())
			.filter(t -> type.isAssignableFrom(t.actionData().get().getClass()))
			.findFirst()
			.orElse(emptyToken());
		return data(state, outToken);
	}

}
