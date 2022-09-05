package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class Flows<S, V0> {
	private final Collection<Flow<S, V0>> flows;

	private Flows(Collection<Flow<S, V0>> flows) {
		this.flows = requireNonNull(flows, "flows must be non-null!");
	}

	public static <S, V0> Flows<S, V0> of(Collection<Flow<S, V0>> flows) {
		return new Flows<>(flows);
	}

	public Behavior<S, V0, V0> asBehavior(State<S, V0> definedState, State<S, V0> defaultState) {

		return unitedBehavior(new OnlyOneBehaviorMayAct<>(), asTransitions(definedState, defaultState));
	}

	public List<Transition<S, V0>> asTransitions(State<S, V0> definedState, State<S, V0> defaultState) {
		return asCollection().stream().map(e -> e.asTransition(definedState, defaultState)).collect(Collectors.toList());
	}

	public Collection<Flow<S, V0>> asCollection() {
		return flows;
	}
}
