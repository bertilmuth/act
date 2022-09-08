package org.requirementsascode.act.core.testdata;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.ClearValue;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class LastOneWhoActsWins<S,V> implements MergeStrategy<S,V> {
	@Override
	public Data<S, V> merge(Data<S, V> dataBefore, List<Data<S, V>> dataAfters) {
		Data<S, V> lastAfterOrStateBefore = dataAfters.isEmpty() ? stateOf(dataBefore) : lastOf(dataAfters);
		return lastAfterOrStateBefore;
	}

	private Data<S, V> stateOf(Data<S, V> dataBefore) {
		Data<S, V> stateWithoutValue = new ClearValue<S, V, V>().actOn(dataBefore);
		return stateWithoutValue;
	}
	
	private Data<S, V> lastOf(List<Data<S, V>> dataAfters) {
		return dataAfters.get(dataAfters.size() - 1);
	}
}
