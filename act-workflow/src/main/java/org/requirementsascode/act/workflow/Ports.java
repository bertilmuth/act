package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Ports {
	private final List<Node> nodes;

	Ports(List<Node> nodes) {
		this.nodes = requireNonNull(nodes, "nodes must be non-null!");
	}

	Stream<State<WorkflowState, Token>> asStates() {
		Stream<State<WorkflowState, Token>> statesStream = this.stream().map(e -> e.asState());
		return statesStream;
	}

	public Stream<Node> stream() {
		return nodes.stream();
	}
}
