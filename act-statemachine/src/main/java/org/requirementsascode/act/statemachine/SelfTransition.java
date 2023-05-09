package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Behavior;

public class SelfTransition<S, V0> implements Transitionable<S, V0> {
	private final State<S, V0> state;
	private final Behavior<S, V0, V0> transitionBehavior;

	SelfTransition(State<S, V0> state, Behavior<S, V0, V0> transitionBehavior) {
		this.state = state;
		this.transitionBehavior = requireNonNull(transitionBehavior, "transitionBehavior must be non-null");
	}

	public State<S, V0> state() {
		return state;
	}

	public Behavior<S, V0, V0> transitionBehavior() {
		return transitionBehavior;
	}

	@Override
	public Transition<S, V0> asTransition(Statemachine<S, V0> owningStatemachine) {
		return transition(state(), state(), transitionBehavior(), (sm, f, t) -> Behavior.identity());
	}
}