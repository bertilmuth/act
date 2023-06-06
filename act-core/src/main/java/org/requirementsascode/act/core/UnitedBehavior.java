package org.requirementsascode.act.core;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;

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
		Data<S, V> noOpBefore = noOp(before);
		NoOpTest<S, V> noOpTest = new NoOpTest<>(noOpBefore);
		
		Data<S, V> mergedData = behaviors.stream()
			.map(b -> b.actOn(before))
			.reduce(noOpBefore,
				(beforeNow, now) -> merge(noOpTest, before, beforeNow, now));

		return mergedData;
	}
	
	private Data<S, V> merge(NoOpTest<S, V> noOpTest, Data<S, V> before, Data<S, V> beforeNow, Data<S, V> now) {
		Data<S, V> mergedData;
		
		if (noOpTest.test(beforeNow)) {
			// Nothing got done before --> take data now (left identity)
			mergedData = now;
		} else if (noOpTest.test(now)) {
			// Nothing got done now --> take data before (right identity)
			mergedData = beforeNow;
		} else {
			// Custom merge for everything else
			mergedData = mergeStrategy.merge(before, beforeNow, now);
		}
		return mergedData;
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