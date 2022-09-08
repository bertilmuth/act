package org.requirementsascode.act.statemachine.merge;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.ClearValue;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class OnlyOneBehaviorMayAct<S, V> implements MergeStrategy<S, V> {
	@Override
	public Data<S, V> merge(Data<S, V> dataBefore, List<Data<S, V>> dataAfters) {
		Data<S, V> result;
		
		if(dataAfters.size() > 1) {
			// more than one behavior that can act --> this is not acceptable, throw exception
			throw new MoreThanOneBehaviorActed(
				"Only 1 behavior may act, but more than one can. Data before: " + dataBefore + " Data afters: " + dataAfters);
		} else if(dataAfters.size() == 1) {
			result = firstOf(dataAfters);
		} else {
			result = clearValueOf(dataBefore);
		}
		
		return result;
	}

	private Data<S, V> firstOf(List<Data<S, V>> dataAfters) {
		return dataAfters.get(0);
	}
	
	private Data<S, V> clearValueOf(Data<S, V> dataBefore) {
		Data<S, V> stateWithoutValue = new ClearValue<S, V, V>().actOn(dataBefore);
		return stateWithoutValue;
	}
}
