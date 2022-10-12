package org.requirementsascode.act.statemachine;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;

public class Flows<S, V0> implements AsBehavior<S, V0>{
	private final List<Flow<S, V0>> flows;

	private Flows(List<Flow<S, V0>> flows) {
		this.flows = requireNonNull(flows, "flows must be non-null!");
	}

	static <S, V0> Flows<S, V0> flows(List<Flow<S, V0>> flows) {
		return new Flows<>(flows);
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		return asTransitions(owningStatemachine).asBehavior(owningStatemachine);
	}

	public Transitions<S, V0> asTransitions(Statemachine<S, V0> owningStatemachine) {
		List<Transition<S, V0>> transitionsList = asList().stream().map(e -> e.asTransition(owningStatemachine))
				.collect(Collectors.toList());
		return Transitions.transitions(transitionsList);
	}

	public List<Flow<S, V0>> asList() {
		return unmodifiableList(flows);
	}
}
