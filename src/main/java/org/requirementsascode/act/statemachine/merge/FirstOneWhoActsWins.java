package org.requirementsascode.act.statemachine.merge;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class FirstOneWhoActsWins<S, V> implements MergeStrategy<S, V> {
	@Override
	public Data<S, V> merge(Data<S, V> outputBefore, Data<S, V> outputNow) {
		return outputBefore;
	}
}
