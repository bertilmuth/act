package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Ports{
	private final List<Port<?>> ports;

	Ports(List<Port<?>> nodes) {
		this.ports = requireNonNull(nodes, "nodes must be non-null!");
	}

	Stream<State<WorkflowState, Token>> asStates() {
		return this.stream().map(Port::asState);
	}

	public Stream<Port<?>> stream() {
		return ports.stream();
	}
	
	public State<WorkflowState, Token> asState() {
		return state("Ports_" + this, this::areTokensInAllPorts, portsBehavior());
	}
	
	private Behavior<WorkflowState, Token, Token> portsBehavior(){
		@SuppressWarnings("unchecked")
		State<WorkflowState,Token>[] statesArray = asStates().toArray(State[]::new);
		return Statemachine.builder()
			.states(statesArray)
			.transitions()
			.build();
	}
	
	private boolean areTokensInAllPorts(WorkflowState state) {
		boolean result = asStates()
			.map(State::invariant)
			.filter(p -> p.test(state))
			.count() == ports.size();
		return result;
	}
}
