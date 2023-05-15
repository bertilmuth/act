package org.requirementsascode.act.statemachine;

import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Function;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Transition.ToStateEntryBehaviorSupplier;

class CheckedEntryBehaviorSupplier<S,V0> implements ToStateEntryBehaviorSupplier<S,V0>{
	private final Function<Statemachine<S, V0>, Behavior<S, V0, V0>> toStateBehaviorSupplier;

	public CheckedEntryBehaviorSupplier(Function<Statemachine<S,V0>, Behavior<S,V0,V0>> toStateBehaviorSupplier) {
		this.toStateBehaviorSupplier = toStateBehaviorSupplier;
	}
	
	@Override
	public Behavior<S, V0, V0> supply(Statemachine<S, V0> sm, Transition<S, V0> transition) {
		State<S, V0> fromState = transition.fromState();
		State<S, V0> toState = transition.toState();
		Behavior<S, V0, V0> toStateBehavior = toStateBehaviorSupplier.apply(sm);
		
		return new TriggeredBehavior<>(inCase(toState::matchesStateIn, 
			toStateBehavior, errorIfNotInToState(fromState, toState)));
	}
	
	private Behavior<S, V0,V0> errorIfNotInToState(State<S,V0> fromState, State<S,V0> toState) {
		return d -> {
			throw new IllegalStateException("Tried transition from " + fromState + " to " + toState + ", but invariant was false in toState! Data: " + d);
		};
	}
}
