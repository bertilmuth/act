package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.NoOp;

public class Transition<S, V0> implements Behavioral<S,V0>, Transitionable<S, V0> {
	private final State<S, V0> fromState;
	private final State<S, V0> toState;
	private final Behavior<S, V0, V0> transitionBehavior;

	private Transition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0, V0> transitionBehavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.toState = requireNonNull(toState, "toState must be non-null");
		this.transitionBehavior = requireNonNull(transitionBehavior, "transitionBehavior must be non-null");
	}

	static <S, V0> Transition<S, V0> transition(State<S, V0> fromState, State<S, V0> toState,
			Behavior<S, V0, V0> transitionBehavior) {
		return new Transition<>(fromState, toState, transitionBehavior);
	}

	public State<S, V0> fromState() {
		return fromState;
	}

	public State<S, V0> toState() {
		return toState;
	}
	
	public Behavior<S, V0, V0> transitionBehavior() {
		return transitionBehavior;
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
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> sm) {	
		Behavior<S, V0, V0> toStateBehavior = toStateBehavior(sm);
		
		return inCase(fromState()::matchesStateIn,
			transitionBehavior().andThen(
				inCase(this::hasFired, 
					inCase(this::isInToState, 
						inCase(this::toStateEqualsFromState, 
							new NoOp<>(), 
							toStateBehavior), 
						this::errorIfNotInToState))));
	}

	private boolean isInToState(Data<S, V0> d) {
		return toState().matchesStateIn(d);
	}
	
	private boolean toStateEqualsFromState(Data<S,V0> data) {
		return toState().equals(fromState());
	}
	
	private Behavior<S, V0, V0> toStateBehavior(Statemachine<S, V0> sm) {
		return d -> inCase(x -> sm.isRecursive(),
			sm,
			toState().asBehavior(sm)
		).actOn(d);
	}

	private boolean hasFired(Data<?, ?> data) {
		return data.value().isPresent();
	}
	
	private Data<S, V0> errorIfNotInToState(Data<S, V0> d) {
		throw new IllegalStateException("Tried transition from " + fromState() + " to " + toState()
				+ ", but invariant was false in toState! Data: " + d);
	}
}