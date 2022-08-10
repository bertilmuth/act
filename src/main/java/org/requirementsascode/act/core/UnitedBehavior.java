package org.requirementsascode.act.core;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.NothingGotDone.*;

import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.core.merge.MergeStrategy;

public class UnitedBehavior<S, V> implements Behavior<S, V> {
	private final MergeStrategy<S, V> mergeStrategy;
	private final List<? extends Behavior<S, V>> behaviors;

	private UnitedBehavior(MergeStrategy<S, V> mergeStrategy, List<? extends Behavior<S, V>> behaviors) {
		this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
		this.behaviors = requireNonNull(behaviors, "behaviors must be non-null!");
	}

	@SafeVarargs
	public static <S, V> UnitedBehavior<S, V> unitedBehavior(MergeStrategy<S, V> mergeStrategy,
		Behavior<S, V>... behaviors) {
		return new UnitedBehavior<>(mergeStrategy, asList(behaviors));
	}

	public static <S, V> UnitedBehavior<S, V> unitedBehavior(MergeStrategy<S, V> mergeStrategy,
		List<? extends Behavior<S, V>> behaviors) {
		return new UnitedBehavior<>(mergeStrategy, behaviors);
	}

	@Override
	public Data<S, V> actOn(Data<S, V> before) {
		Data<S, V> nothingDoneAtFirst = new DoNothing<S, V>().actOn(before);
		
		Data<S, V> after = behaviors.stream()
			.map(b -> b.actOn(before))
			.reduce(nothingDoneAtFirst,
				(dataBefore, dataNow) -> merge(nothingGotDone(before), dataBefore, dataNow));

		return after;
	}

	private Data<S, V> merge(NothingGotDone<S, V> nothingGotDone, Data<S, V> dataBefore, Data<S, V> dataNow) {
		Data<S, V> mergedData;

		if (nothingGotDone.test(dataBefore)) {
			// Nothing got done before --> take data now
			mergedData = dataNow;
		} else if (nothingGotDone.test(dataNow)) {
			// Nothing got done now --> take data before
			mergedData = dataBefore;
		} else {
			// Custom merge for everything else
			mergedData = mergeStrategy.merge(dataBefore, dataNow);
		}
		return mergedData;
	}

	public List<Behavior<S, ? extends V>> behaviors() {
		return Collections.unmodifiableList(behaviors);
	}
}