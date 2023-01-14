package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Ports implements Named{
	private final List<Port<?>> ports;
	private final String name;

	Ports(List<Port<?>> ports) {
		this.ports = requireNonNull(ports, "ports must be non-null!");
		this.name = createName(ports);
	}

	public Stream<Port<?>> stream() {
		return ports.stream();
	}
	
	@Override
	public String name() {
		return name;
	}
	
	State<WorkflowState, Token> asState() {
		return state(name(), this::areTokensInAllPorts, portsBehavior());
	}
	
	Stream<State<WorkflowState, Token>> asStates() {
		return this.stream().map(Port::asState);
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
		long nrOfActivePorts = asStates()
			.map(State::invariant)
			.filter(p -> p.test(state))
			.count();
		boolean result = nrOfActivePorts == ports.size();
		return result;
	}
	
	private String createName(List<Port<?>> ports) {
		return stream().map(Port::name).reduce("Ports", (n1, n2) -> n1 + "_" + n2);
	}
}
