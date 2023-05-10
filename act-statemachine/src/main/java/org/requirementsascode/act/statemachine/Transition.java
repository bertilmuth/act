package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Transition<S, V0> implements Behavioral<S,V0>, Transitionable<S, V0> {
	private final State<S, V0> fromState;
	private final State<S, V0> toState;
	private final Behavior<S, V0, V0> transitionBehavior;
	private StateEntryBehaviorSupplier<S,V0> toStateEntryBehaviorSupplier;

	Transition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0, V0> transitionBehavior) {
		this(fromState, toState, transitionBehavior, Transition::defaultToStateBehaviorProvider);
	}
	
	Transition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0, V0> transitionBehavior, StateEntryBehaviorSupplier<S,V0> toStateEntryBehaviorSupplier) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.toState = requireNonNull(toState, "toState must be non-null");
		this.transitionBehavior = requireNonNull(transitionBehavior, "transitionBehavior must be non-null");
		this.toStateEntryBehaviorSupplier = requireNonNull(toStateEntryBehaviorSupplier, "toStateEntryBehaviorSupplier must be non-null");
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
		Behavior<S, V0, V0> toStateEntryBehavior = toStateEntryBehaviorSupplier.supply(sm, fromState(), toState());
		
		return triggeredBehavior(
			inCase(fromState()::matchesStateIn,
			transitionBehavior().andThen(triggeredBehavior(toStateEntryBehavior))
		));
	}

	Behavior<S, V0, V0> triggeredBehavior(Behavior<S, V0, V0> behavior) {
		return inCase(Transition::triggerIsPresent, behavior);
	}
	
	private static boolean triggerIsPresent(Data<?, ?> data) {
		return data.value().isPresent();
	}
	
	interface StateEntryBehaviorSupplier<S,V0>{
		Behavior<S,V0,V0> supply(Statemachine<S, V0> statemachine, State<S,V0> fromState, State<S, V0> toState);
	}
	
	private static <S,V0> Behavior<S, V0, V0> defaultToStateBehaviorProvider(Statemachine<S, V0> sm, State<S,V0> fromState, State<S,V0> toState) {
		return inCase(toState::matchesStateIn, toState.asBehavior(sm), errorIfNotInToState(fromState, toState));
	}
	
	private static <S,V0> Behavior<S, V0,V0> errorIfNotInToState(State<S,V0> fromState, State<S,V0> toState) {
		return d -> {
			throw new IllegalStateException("Tried transition from " + fromState + " to " + toState + ", but invariant was false in toState! Data: " + d);
		};
	}
}