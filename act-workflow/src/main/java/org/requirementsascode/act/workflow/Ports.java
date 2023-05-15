package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;

public class Ports implements Named{
	private final List<Port<?>> ports;
	private final String name;
	private State<WorkflowState, Token> state;

	Ports(List<Port<?>> ports) {
		this.ports = requireNonNull(ports, "ports must be non-null!");
		this.name = createName(ports);
		this.state = createState(name);
	}

	public Stream<Port<?>> stream() {
		return ports.stream();
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public Optional<? extends ActionData> firstActionData(WorkflowState state) {
		return stream()
			.filter(state::areTokensIn)
			.map(p -> p.firstActionData(state).get())
			.findFirst();
	}
	
	State<WorkflowState, Token> asOneState() {
		return state;
	}
	
	Stream<State<WorkflowState, Token>> asStates() {
		return this.stream().map(Port::asState);
	}
	
	private State<WorkflowState, Token> createState(String name) {
		return state(name, this::areTokensInAllPorts, Behavior.identity());
	}
	
	private boolean areTokensInAllPorts(WorkflowState state) {
		long nrOfActivePorts = asStates()
			.map(State::invariant)
			.filter(p -> p.test(state))
			.count();
		return nrOfActivePorts == ports.size();
	}
	
	private String createName(List<Port<?>> ports) {
		return stream().map(Port::name).reduce("Ports", (n1, n2) -> n1 + "_" + n2);
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Named && 
			name().equals(((Named)obj).name());
	}
	
	@Override
	public int hashCode() {
		return name().hashCode();
	}
}
