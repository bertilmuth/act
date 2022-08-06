package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.HandleChange;

public class Transition<S, V extends V0, V0> implements Behavior<S, V0>{
	private final State<S, V0> fromState;
	private final State<S, V0> toState;
	private final Behavior<S, V0> transitionBehavior;
	private final HandleChange<S, V0> handleChange;

	private Transition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0> behavior, HandleChange<S,V0> handleChange) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.toState = requireNonNull(toState, "toState must be non-null");
		this.handleChange = requireNonNull(handleChange, "handleChange must be non-null");
		requireNonNull(behavior, "behavior must be non-null");

		this.transitionBehavior = createTransitionBehavior(fromState, behavior, handleChange);
	}

	public static <S, V extends V0, V0> Transition<S, V, V0> transition(State<S, V0> fromState, State<S, V0> toState,
		Behavior<S, V0> behavior) {
		return transition(fromState, toState, behavior, (before, after) -> {});
	}
	
	public static <S, V extends V0, V0> Transition<S, V, V0> transition(State<S, V0> fromState, State<S, V0> toState,
			Behavior<S, V0> behavior, HandleChange<S, V0> handleChange) {
			return new Transition<>(fromState, toState, behavior, handleChange);
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

	private Behavior<S, V0> createTransitionBehavior(State<S, V0> fromState, Behavior<S, V0> behavior, HandleChange<S, V0> handleChange) {
		return inCase(input -> fromState.matchesStateIn(input), behavior
			.andHandleChange(this::errorIfNotInToStateIfTransitionFired)
			.andHandleChange(this::handleChangeIfTransitionFired)
			.andThen(getToState()));
	}
	
	private void errorIfNotInToStateIfTransitionFired(Data<S, V0> before, Data<S, V0> after) {
		if(hasTransitionFired(after) && !getToState().matchesStateIn(after)){
			throw new IllegalStateException(
					"Tried transition from " + fromState + " to " + toState + ", but invariant was false!\nbefore: " + before + "\nafter: " + after);
		}
	}
	
	private void handleChangeIfTransitionFired(Data<S, V0> before, Data<S, V0> after) {
		if(hasTransitionFired(after)){
			handleChange.handleChange(before, after);
		}
	}
	
	private boolean hasTransitionFired(Data<S, V0> after) {
		return after.getValue() != null;
	}
}