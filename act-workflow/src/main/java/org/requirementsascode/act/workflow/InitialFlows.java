package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InitialFlows{
	private final List<InitialFlow> initialFlows;

	InitialFlows(List<Node> initialNodes) {
		requireNonNull(initialNodes, "initialNodes must be non-null!");
		this.initialFlows = createInitialFlowsFrom(initialNodes);
	}

	public Stream<InitialFlow> stream() {
		return initialFlows.stream();
	}
	
	private List<InitialFlow> createInitialFlowsFrom(List<Node> initialNodes) {
		return initialNodes.stream()
			.map(InitialFlow::new)
			.collect(Collectors.toList());
	}
}
