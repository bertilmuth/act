package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Behavior;

public class ExitFlow<S, V0> implements Transitionable<S, V0>{
	private final State<S, V0> fromState;
	private final Behavior<S, V0, V0> exitBehavior;

	private ExitFlow(State<S, V0> fromState, Behavior<S, V0, V0> exitBehavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.exitBehavior = requireNonNull(exitBehavior, "exitBehavior must be non-null");
	}

	static <S, V0> ExitFlow<S, V0> exitFlow(State<S, V0> fromState, Behavior<S, V0, V0> exitBehavior) {
		return new ExitFlow<>(fromState, exitBehavior);
	}

	@Override
	public Transition<S, V0> asTransition(Statemachine<S, V0> owningStatemachine) {
		State<S, V0> finalState = owningStatemachine.finalState();
		return transition(fromState, finalState, exitBehavior);
	}
}