package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Change;
import org.requirementsascode.act.core.Data;

public class Transition<S, V0> implements AsBehavior<S, V0> {
	private final State<S, V0> fromState;
	private final State<S, V0> toState;
	private final Behavior<S, V0, V0> behavior;

	private Transition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0, V0> behavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.toState = requireNonNull(toState, "toState must be non-null");
		this.behavior = requireNonNull(behavior, "behavior must be non-null");
	}

	static <S, V0> Transition<S, V0> transition(State<S, V0> fromState, State<S, V0> toState,
			Behavior<S, V0, V0> behavior) {
		return new Transition<>(fromState, toState, behavior);
	}

	public State<S, V0> fromState() {
		return fromState;
	}

	public State<S, V0> toState() {
		return toState;
	}

	@Override
	public String toString() {
		return "Transition [fromState=" + fromState + ", toState=" + toState + "]";
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		Behavior<S, V0, V0> toStateBehavior = toState().asBehavior(owningStatemachine);
		
		return inCase(before -> fromState.matchesStateIn(before),
				behavior.andHandleChangeWith(this::errorIfNotInToStateIfTransitionFired)
					.andThen(toStateBehavior));
	}

	private Data<S, V0> errorIfNotInToStateIfTransitionFired(Change<S, V0, V0> c) {
		if (hasFired(c.after()) && !toState().matchesStateIn(c.after())) {
			throw new IllegalStateException("Tried transition from " + fromState + " to " + toState
					+ ", but invariant was false!\nbefore: " + c.before() + "\nafter: " + c.after());
		}
		return c.after();
	}

	public static boolean hasFired(Data<?, ?> data) {
		return data.value().isPresent();
	}
}