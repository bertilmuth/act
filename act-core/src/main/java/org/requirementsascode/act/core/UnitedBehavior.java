package org.requirementsascode.act.core;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
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
		List<Data<S, V>> datasAfter = behaviors.stream()
				.map(b -> b.actOn(dataBefore))
				.filter(hasStateChangedFrom(dataBefore).or(Behavior::hasActed))
				.collect(Collectors.toList());

		Data<S, V> mergedData;

		if (datasAfter.isEmpty()) {
			mergedData = noOp(dataBefore);
		} else {
			mergedData = merge(dataBefore, datasAfter);
		}

		return mergedData;
	}

	private Predicate<Data<S, V>> hasStateChangedFrom(Data<S, V> dataBefore) {
		return d -> d.state() != null && !d.state().equals(dataBefore.state());
	}

	private Data<S, V> noOp(Data<S, V> dataBefore) {
		return new NoOp<S, V, V>().actOn(dataBefore);
	}

	private Data<S, V> merge(Data<S, V> dataBefore, List<Data<S, V>> datasAfter) {
		return mergeStrategy.merge(dataBefore, datasAfter);
	}

	public List<Behavior<S, ? extends V, ? extends V>> behaviors() {
		return Collections.unmodifiableList(behaviors);
	}
}