package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class DataFlows{
	private final List<DataFlow<?>> dataFlows;

	DataFlows(List<DataFlow<?>> dataFlows) {
		this.dataFlows = requireNonNull(dataFlows, "dataFlows must be non-null!");
	}

	public Stream<DataFlow<?>> stream() {
		return dataFlows.stream();
	}
}
