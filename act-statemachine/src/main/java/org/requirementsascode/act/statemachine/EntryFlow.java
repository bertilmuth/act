package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.Optional;

import org.requirementsascode.act.core.Behavior;

public class EntryFlow<S, V0> implements Transitionable<S, V0> {
	private final State<S, V0> toState;
	private final Behavior<S, V0, V0> entryBehavior;

	EntryFlow(State<S, V0> toState, Behavior<S, V0, V0> entryBehavior) {
		this.toState = toState; // May be null
		this.entryBehavior = requireNonNull(entryBehavior, "entryBehavior must be non-null");
	}

	public Optional<State<S, V0>> toState() {
		return Optional.ofNullable(toState);
	}

	public Behavior<S, V0, V0> entryBehavior() {
		return entryBehavior;
	}

	@Override
	public Transition<S, V0> asTransition(Statemachine<S, V0> owningStatemachine) {
		State<S, V0> initialState = owningStatemachine.initialState();
		State<S, V0> toStateOrAnyDefinedState = toState().orElse(owningStatemachine.definedState());
		return transition(initialState, toStateOrAnyDefinedState, entryBehavior())
			.asTransition(owningStatemachine);
	}
}