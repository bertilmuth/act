package org.requirementsascode.act.workflow.function;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.emptyToken;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

class SelectOneTokenByType<T> implements Behavior<WorkflowState, Part, Token> {
	private final Class<T> type;

	public SelectOneTokenByType(Class<T> type) {
		this.type = requireNonNull(type, "type must be non-null!");

	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Part> inputData) {
		WorkflowState state = inputData.state();
		
		assert(inputData.value().isPresent());
		Ports inPorts = inputData.value().get().inPorts();
		
		Token outToken = inPorts.stream()
			.flatMap(p -> p.tokens(state))
			.filter(t -> t.actionData().isPresent())
			.filter(t -> type.isAssignableFrom(t.actionData().get().getClass()))
			.findFirst()
			.orElse(emptyToken());
		return data(state, outToken);
	}

}
