package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class DataFlows{
	private final List<DataFlow> dataFlows;

	private DataFlows(List<DataFlow> dataFlows) {
		this.dataFlows = requireNonNull(dataFlows, "dataFlows must be non-null!");
	}

	static DataFlows tokenFlows(List<DataFlow> dataFlows) {
		return new DataFlows(dataFlows);
	}

	public Stream<DataFlow> stream() {
		return dataFlows.stream();
	}
}
