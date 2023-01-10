package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;

public class Port<T extends ActionData> implements Node {
	private final String name;
	private final Class<T> type;

	Port(String name, Class<T> type) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.type = requireNonNull(type, "type must be non-null!");		
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public Class<T> type() {
		return type;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(name(), this::areTokensInPort, Behavior.identity());
	}
	
	private boolean areTokensInPort(WorkflowState state) {
		return state.areTokensIn(this);
	}

	@Override
	public String toString() {
		return "Port[" + name + "]";
	}
}
