package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;

public class Transitions<S, V0> implements AsBehavior<S, V0> {
	private final List<Transition<S, V0>> transitions;

	private Transitions(List<Transition<S, V0>> transitions) {
		this.transitions = requireNonNull(transitions, "transitions must be non-null!");
	}

	static <S, V0> Transitions<S, V0> transitions(List<Transition<S, V0>> transitions) {
		return new Transitions<>(transitions);
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		return unitedBehavior(owningStatemachine.mergeStrategy(), transitionBehaviors(owningStatemachine));
	}

	private List<Behavior<S, V0, V0>> transitionBehaviors(Statemachine<S, V0> owningStatemachine) {
		List<Behavior<S, V0, V0>> behaviors = this.stream()
			.map(e -> e.asBehavior(owningStatemachine))
			.collect(Collectors.toList());
		return behaviors;
	}

	public Stream<Transition<S, V0>> stream() {
		return transitions.stream();
	}
}
