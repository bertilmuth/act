package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Ports {
	private final List<Port<?>> ports;

	Ports(List<Port<?>> nodes) {
		this.ports = requireNonNull(nodes, "nodes must be non-null!");
	}

	Stream<State<WorkflowState, Token>> asStates() {
		Stream<State<WorkflowState, Token>> statesStream = this.stream().map(e -> e.asState());
		return statesStream;
	}

	public Stream<Port<?>> stream() {
		return ports.stream();
	}
}
