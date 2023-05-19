package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.core.Behavior;

public class TriggeredTransition<S, V0> implements Transitionable<S, V0> {
	private final State<S, V0> fromState;
	private final State<S, V0> toState;
	private final Behavior<S, V0, V0> transitionBehavior;

	protected TriggeredTransition(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0, V0> transitionBehavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null!");
		this.toState = requireNonNull(toState, "toState must be non-null!");
		this.transitionBehavior = requireNonNull(transitionBehavior, "flowBehavior must be non-null");
	}

	@Override
	public Transition<S, V0> asTransition(Statemachine<S, V0> owningStatemachine) {
		return new Transition<>(fromState, toState, new TriggeredBehavior<>(transitionBehavior));
	}
}