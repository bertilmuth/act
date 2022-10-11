package org.requirementsascode.act.statemachine.merge;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class OnlyOneBehaviorMayAct<S, V> implements MergeStrategy<S, V> {
	@Override
	public Data<S, V> merge(List<Data<S, V>> datas) {
		// more than one behavior that can act --> this is not acceptable, throw
		// exception
		throw new MoreThanOneBehaviorActed(
				"Only 1 behavior may act, but more than one can. Datas: " + datas);
	}
}
