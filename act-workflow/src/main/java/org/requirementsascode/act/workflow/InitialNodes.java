package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InitialNodes{
	private final List<InitialNode> initialNodes;

	InitialNodes(List<Action> nodes) {
		requireNonNull(nodes, "nodes must be non-null!");
		this.initialNodes = createInitialNodes(nodes);
	}

	public Stream<InitialNode> stream() {
		return initialNodes.stream();
	}
	
	private List<InitialNode> createInitialNodes(List<Action> nodes) {
		return nodes.stream()
			.map(InitialNode::new)
			.collect(Collectors.toList());
	}
}
