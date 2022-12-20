package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartFlows{
	private final List<StartFlow> initialFlows;

	StartFlows(List<Node> initialNodes) {
		requireNonNull(initialNodes, "initialNodes must be non-null!");
		this.initialFlows = createInitialFlowsFrom(initialNodes);
	}

	public Stream<StartFlow> stream() {
		return initialFlows.stream();
	}
	
	private List<StartFlow> createInitialFlowsFrom(List<Node> initialNodes) {
		return initialNodes.stream()
			.map(StartFlow::new)
			.collect(Collectors.toList());
	}
}
