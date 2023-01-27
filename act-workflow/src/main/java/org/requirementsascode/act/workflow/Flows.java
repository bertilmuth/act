package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class Flows{
	private final List<Flow> flowsList;

	Flows(List<Flow> flowsList) {
		this.flowsList = requireNonNull(flowsList, "flowsList must be non-null!");
	}

	public Stream<Flow> stream() {
		return flowsList.stream();
	}
}
