package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartFlows{
	private final List<StartFlow> startFlows;

	StartFlows(List<Node> startNodes) {
		requireNonNull(startNodes, "startNodes must be non-null!");
		this.startFlows = createStartFlowsFrom(startNodes);
	}

	public Stream<StartFlow> stream() {
		return startFlows.stream();
	}
	
	private List<StartFlow> createStartFlowsFrom(List<Node> startNodes) {
		return startNodes.stream()
			.map(StartFlow::new)
			.collect(Collectors.toList());
	}
}
