package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.NothingGotDone.*;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.merge.MergeStrategy;

public class UnitedBehavior<S, V> implements Behavior<S, V, V> {
	private final MergeStrategy<S, V> mergeStrategy;
	private final List<? extends Behavior<S, V, V>> behaviors;

	private UnitedBehavior(MergeStrategy<S, V> mergeStrategy, List<? extends Behavior<S, V, V>> behaviors) {
		this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
		this.behaviors = requireNonNull(behaviors, "behaviors must be non-null!");
	}

	@SafeVarargs
	public static <S, V> UnitedBehavior<S, V> unitedBehavior(MergeStrategy<S, V> mergeStrategy,
			Behavior<S, V, V>... behaviors) {
		return new UnitedBehavior<>(mergeStrategy, asList(behaviors));
	}

	public static <S, V> UnitedBehavior<S, V> unitedBehavior(MergeStrategy<S, V> mergeStrategy,
			List<? extends Behavior<S, V, V>> behaviors) {
		return new UnitedBehavior<>(mergeStrategy, behaviors);
	}

	@Override
	public Data<S, V> actOn(Data<S, V> dataBefore) {
		NothingGotDone<S, V> nothingGotDone = nothingGotDone(dataBefore);

		List<Data<S, V>> dataAfters = behaviors.stream()
				.map(b -> b.actOn(dataBefore))
				.filter(nothingGotDone.negate())
				.collect(Collectors.toList());

		Data<S, V> mergedData;

		if (dataAfters.isEmpty()) {
			mergedData = keepStateOf(dataBefore);
		} else if (dataAfters.size() == 1) {
			mergedData = singleElementOf(dataAfters);
		} else {
			mergedData = merge(dataBefore, dataAfters);
		}

		return mergedData;
	}

	private Data<S, V> keepStateOf(Data<S, V> dataBefore) {
		Data<S, V> stateWithoutValue = new KeepState<S, V, V>().actOn(dataBefore);
		return stateWithoutValue;
	}

	private Data<S, V> singleElementOf(List<Data<S, V>> dataAfters) {
		return dataAfters.get(0);
	}

	private Data<S, V> merge(Data<S, V> dataBefore, List<Data<S, V>> dataAfters) {
		return mergeStrategy.merge(dataBefore, dataAfters);
	}

	public List<Behavior<S, ? extends V, ? extends V>> behaviors() {
		return Collections.unmodifiableList(behaviors);
	}
}