package org.requirementsascode.act.core.testdata;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class LastOneWhoActsWins<S,V> implements MergeStrategy<S,V> {
	@Override
	public Data<S, V> merge(Data<S, V> before, Data<S, V> beforeNow, Data<S, V> now) {
		return now;
	}
}
