package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;

public class States<S, V0> implements Behavioral<S, V0>{
	private final List<State<S, V0>> states;

	States(List<State<S, V0>> states) {
		this.states = requireNonNull(states, "states must be non-null!");
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		return unitedBehavior(owningStatemachine.mergeStrategy(), stateBehaviors(owningStatemachine));
	}

	private List<Behavior<S, V0, V0>> stateBehaviors(Statemachine<S, V0> owningStatemachine) {
		List<Behavior<S, V0, V0>> behaviors = this.stream()
			.map(s -> s.asBehavior(owningStatemachine))
			.collect(Collectors.toList());
		return behaviors;
	}

	public Stream<State<S, V0>> stream() {
		return states.stream();
	}
}
