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
	public Data<S, V> actOn(Data<S, V> before) {
		Data<S, V> noOpOnBefore = noOp(before);
		Predicate<Data<S, V>> hasChanged = new NoOpTest<>(noOpOnBefore).negate();
		
		List<Data<S, V>> datasAfter = behaviors.stream()
				.map(b -> b.actOn(before))
				.filter(hasChanged)
				.collect(Collectors.toList());

		Data<S, V> mergedData;

		if (datasAfter.isEmpty()) {
			mergedData = noOpOnBefore;
		} else {
			mergedData = merge(before, datasAfter);
		}

		return mergedData;
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
	
	private static class NoOpTest<S,V> implements Predicate<Data<S, V>> {
		private final Data<S, V> noOpOnBefore;
		
		private NoOpTest(Data<S, V> noOpOnBefore) {
			this.noOpOnBefore = noOpOnBefore;
		}
		
		@Override
		public boolean test(Data<S, V> before) {
			return noOpOnBefore.equals(before);
		}
	}
}