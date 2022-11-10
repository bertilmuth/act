package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.merge.MergeStrategy;

public class Flows<S, V0> implements AsBehavior<S, V0>{
	private final List<Flow<S, V0>> flows;
	private final MergeStrategy<S, V0> mergeStrategy;

	private Flows(List<Flow<S, V0>> flows, MergeStrategy<S, V0> mergeStrategy) {
		this.flows = requireNonNull(flows, "flows must be non-null!");
		this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
	}

	static <S, V0> Flows<S, V0> flows(List<Flow<S, V0>> flows, MergeStrategy<S, V0> mergeStrategy) {
		return new Flows<>(flows, mergeStrategy);
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		return asTransitions(owningStatemachine).asBehavior(owningStatemachine);
	}

	public Transitions<S, V0> asTransitions(Statemachine<S, V0> owningStatemachine) {
		List<Transition<S, V0>> transitionsList = this.stream()
				.map(e -> e.asTransition(owningStatemachine))
				.collect(Collectors.toList());
		return Transitions.transitions(transitionsList);
	}

	public Stream<Flow<S, V0>> stream() {
		return flows.stream();
	}
}
