package org.requirementsascode.act.core.testdata;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class LastOneWhoActsWins<S,V> implements MergeStrategy<S,V> {
	@Override
	public Data<S,V> merge(Data<S, V> dataBefore, List<Data<S, V>> datas) {
		return lastOf(datas);
	}
	
	private Data<S, V> lastOf(List<Data<S, V>> datas) {
		return datas.get(datas.size() - 1);
	}
}
