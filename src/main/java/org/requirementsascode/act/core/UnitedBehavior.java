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
	public Data<S, V> actOn(Data<S, V> input) {
		Data<S, V> nothingDoneAtFirst = new DoNothing<S, V>().actOn(input);
		
		Data<S, V> output = behaviors.stream()
			.map(b -> b.actOn(input))
			.reduce(nothingDoneAtFirst,
				(outputBefore, outputNow) -> merge(nothingGotDone(input), outputBefore, outputNow));

		return output;
	}

	private Data<S, V> merge(NothingGotDone<S, V> nothingGotDone, Data<S, V> outputBefore, Data<S, V> outputNow) {
		Data<S, V> mergedOutput;

		if (nothingGotDone.test(outputBefore)) {
			// Nothing got done before --> take output now
			mergedOutput = outputNow;
		} else if (nothingGotDone.test(outputNow)) {
			// Nothing got done now --> take output before
			mergedOutput = outputBefore;
		} else {
			// Custom merge for everything else
			mergedOutput = mergeStrategy.merge(outputBefore, outputNow);
		}
		return mergedOutput;
	}

	public List<Behavior<S, ? extends V>> behaviors() {
		return Collections.unmodifiableList(behaviors);
	}
}