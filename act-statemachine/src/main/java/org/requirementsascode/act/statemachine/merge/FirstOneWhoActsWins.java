package org.requirementsascode.act.statemachine.merge;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class FirstOneWhoActsWins<S, V> implements MergeStrategy<S, V> {
	@Override
	public Data<S,V> merge(Data<S, V> dataBefore, List<Data<S, V>> datas) {
		requireNonNull(datas, "datas must be non-null!");
		return firstOf(datas);
	}
	
	private Data<S, V> firstOf(List<Data<S, V>> datas) {
		return datas.get(0);
	}
}
