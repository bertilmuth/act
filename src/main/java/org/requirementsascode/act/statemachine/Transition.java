package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Transition<S, V extends V0, V0> implements Behavior<S, V0>{
	private final State<S, V0> fromState;
	private final State<S, V0> toState;
	private final Behavior<S, V0> transitionBehavior;

	private Transition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0> behavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.toState = requireNonNull(toState, "toState must be non-null");
		requireNonNull(behavior, "behavior must be non-null");

		this.transitionBehavior = createTransitionBehavior(fromState, behavior);
	}

	public static <S, V extends V0, V0> Transition<S, V, V0> transition(State<S, V0> fromState, State<S, V0> toState,
		Behavior<S, V0> behavior) {
		return new Transition<>(fromState, toState, behavior);
	}

	@Override
	public Data<S, V0> actOn(Data<S, V0> input) {
		Data<S, V0> result = transitionBehavior.actOn(input);
		return result;
	}

	public State<S, V0> getFromState() {
		return fromState;
	}

	public State<S, V0> getToState() {
		return toState;
	}

	@Override
	public String toString() {
		return "Transition [fromState=" + fromState + ", toState=" + toState + "]";
	}

	private Behavior<S, V0> createTransitionBehavior(State<S, V0> fromState, Behavior<S, V0> behavior) {
		return inCase(input -> fromState.matchesStateIn(input), behavior
			.andThen(inCase(this::isNotInToState, this::throwsIllegalStateException, identity()))
				.andThen(getToState()));
	}

	private boolean isNotInToState(Data<S, V0> data) {
		return data.getValue() != null && !getToState().matchesStateIn(data);
	}

	private Data<S, V0> throwsIllegalStateException(Data<S, V0> output) {
		throw new IllegalStateException(
			"Tried transition from " + fromState + " to " + toState + ", but invariant was false -> output: " + output);
	}
}