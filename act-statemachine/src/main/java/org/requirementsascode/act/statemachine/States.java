package org.requirementsascode.act.statemachine;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class States<S, V0> implements AsBehavior<S, V0>{
	private final List<State<S, V0>> states;

	private States(List<State<S, V0>> states) {
		this.states = requireNonNull(states, "states must be non-null!");
	}

	static <S, V0> States<S, V0> states(List<State<S, V0>> states) {
		return new States<>(states);
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		return unitedBehavior(new OnlyOneBehaviorMayAct<>(), stateBehaviors(owningStatemachine));
	}

	private List<Behavior<S, V0, V0>> stateBehaviors(Statemachine<S, V0> owningStatemachine) {
		List<Behavior<S, V0, V0>> behaviors = asList().stream().map(e -> e.asBehavior(owningStatemachine))
			.collect(Collectors.toList());
		return behaviors;
	}

	public List<State<S, V0>> asList() {
		return unmodifiableList(states);
	}
}
