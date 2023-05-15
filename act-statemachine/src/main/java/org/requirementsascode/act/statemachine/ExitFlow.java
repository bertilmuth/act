package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.core.Behavior;

public class ExitFlow<S, V0> implements Transitionable<S, V0>{
	private final State<S, V0> fromState;
	private final Behavior<S, V0, V0> exitBehavior;

	ExitFlow(State<S, V0> fromState, Behavior<S, V0, V0> exitBehavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.exitBehavior = requireNonNull(exitBehavior, "exitBehavior must be non-null");
	}

	@Override
	public Transition<S, V0> asTransition(Statemachine<S, V0> owningStatemachine) {
		State<S, V0> finalState = owningStatemachine.finalState();
		CheckedEntryBehaviorSupplier<S, V0> entryBehaviorSupplier = new CheckedEntryBehaviorSupplier<>(
				sm -> Behavior.identity());
		return new Transition<>(fromState, finalState, exitBehavior, entryBehaviorSupplier);
	}
}