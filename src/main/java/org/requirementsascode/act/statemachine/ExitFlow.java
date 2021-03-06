package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.Transition.transition;

import org.requirementsascode.act.core.Behavior;

public class ExitFlow<S, V extends V0, V0> implements Flow<S, V0, V0>{
	private final State<S, V0> fromState;
	private final Behavior<S, V0> exitBehavior;

	private ExitFlow(State<S, V0> fromState, Behavior<S, V0> exitBehavior) {
		this.fromState = requireNonNull(fromState, "fromState must be non-null");
		this.exitBehavior = requireNonNull(exitBehavior, "exitBehavior must be non-null");
	}

	public static <S, V extends V0, V0> ExitFlow<S, V, V0> exitFlow(State<S, V0> fromState, Behavior<S, V0> exitBehavior) {
		return new ExitFlow<>(fromState, exitBehavior);
	}

	@Override
	public Transition<S, V0, V0> toTransition(State<S, V0> definedState, State<S, V0> defaultState) {
		return transition(fromState, defaultState, exitBehavior);
	}
}