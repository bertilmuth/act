package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Transition<S, V0> implements Behavioral<S,V0>, Transitionable<S, V0> {
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
	public Transition<S, V0> asTransition(Statemachine<S, V0> owningStatemachine) {
		return this;
	}

	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {				
		return inCase(before -> fromState.matchesStateIn(before),
			behavior
				.andThen(inCase(this::hasFired, 
					inCase(this::isInToState, 
						d -> toStateBehavior(d, owningStatemachine), 
						this::errorIfNotInToState))));
	}

	private boolean isInToState(Data<S, V0> d) {
		return toState().matchesStateIn(d);
	}
	
	public Data<S, V0> toStateBehavior(Data<S, V0> d, Statemachine<S, V0> owningStatemachine) {
		if(!isInDefaultState(d, owningStatemachine)) {
			return toState().asBehavior(owningStatemachine).actOn(d);
		} else {
			return d;
		}
	}

	private boolean hasFired(Data<?, ?> data) {
		return data.value().isPresent();
	}

	private boolean isInDefaultState(Data<S, V0> d, Statemachine<S, V0> owningStatemachine) {
		return owningStatemachine.defaultState().matchesStateIn(d);
	}
	
	private Data<S, V0> errorIfNotInToState(Data<S, V0> d) {
		throw new IllegalStateException("Tried transition from " + fromState + " to " + toState
				+ ", but invariant was false in toState! Data: " + d);
	}
}