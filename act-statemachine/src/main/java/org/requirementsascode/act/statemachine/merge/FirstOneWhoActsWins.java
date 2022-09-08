package org.requirementsascode.act.statemachine.merge;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.NoOp;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class FirstOneWhoActsWins<S, V> implements MergeStrategy<S, V> {
	@Override
	public Data<S, V> merge(Data<S, V> dataBefore, List<Data<S, V>> dataAfters) {
		Data<S, V> firstAfterOrStateBefore = dataAfters.isEmpty() ? stateOf(dataBefore) : dataAfters.get(0);
		return firstAfterOrStateBefore;
	}

	private Data<S, V> stateOf(Data<S, V> dataBefore) {
		Data<S, V> stateWithoutValue = new NoOp<S, V, V>().actOn(dataBefore);
		return stateWithoutValue;
	}
}
