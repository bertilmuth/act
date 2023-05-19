package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class Flows{
	private final List<TokenFlow> flowsList;

	Flows(List<TokenFlow> flowsList) {
		this.flowsList = requireNonNull(flowsList, "flowsList must be non-null!");
	}

	public Stream<TokenFlow> stream() {
		return flowsList.stream();
	}
}
