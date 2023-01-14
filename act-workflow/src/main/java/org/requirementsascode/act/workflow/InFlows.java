package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InFlows{
	private final List<InFlow> inFlows;

	InFlows(List<Port<?>> inPorts) {
		requireNonNull(inPorts, "inPorts must be non-null!");
		this.inFlows = createInFlowsFrom(inPorts);
	}

	public Stream<InFlow> stream() {
		return inFlows.stream();
	}
	
	private List<InFlow> createInFlowsFrom(List<Port<?>> inPorts) {
		return inPorts.stream()
			.map(InFlow::new)
			.collect(Collectors.toList());
	}
}
