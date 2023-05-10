package org.requirementsascode.act.statemachine;

import static org.requirementsascode.act.core.InCase.inCase;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Transition.ToStateEntryBehaviorSupplier;

class DefaultToStateEntryProvider<S,V0> implements ToStateEntryBehaviorSupplier<S,V0>{
	@Override
	public Behavior<S, V0, V0> supply(Statemachine<S, V0> sm, State<S, V0> fromState, State<S, V0> toState) {
		return Transition.triggeredBehavior(inCase(toState::matchesStateIn, toState.asBehavior(sm), errorIfNotInToState(fromState, toState)));
	}
	
	private Behavior<S, V0,V0> errorIfNotInToState(State<S,V0> fromState, State<S,V0> toState) {
		return d -> {
			throw new IllegalStateException("Tried transition from " + fromState + " to " + toState + ", but invariant was false in toState! Data: " + d);
		};
	}
}
