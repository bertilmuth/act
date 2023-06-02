package org.requirementsascode.act.core.merge;

import org.requirementsascode.act.core.Data;

public interface MergeStrategy<S,V> {
	Data<S, V> merge(Data<S, V> before, Data<S, V> beforeNow, Data<S, V> now);
}
