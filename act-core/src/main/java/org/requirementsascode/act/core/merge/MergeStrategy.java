package org.requirementsascode.act.core.merge;

import java.util.List;

import org.requirementsascode.act.core.Data;

public interface MergeStrategy<S,V> {
	Data<S,V> merge(Data<S, V> dataBefore, List<Data<S, V>> datasAfter);
}
