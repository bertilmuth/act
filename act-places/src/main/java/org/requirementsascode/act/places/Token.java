package org.requirementsascode.act.places;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.State;

public class Token<S, V> {
	private final State<S, V> state;

	private Token(State<S, V> state) {
		this.state = requireNonNull(state, "state must be non-null!");
	}

	public static <S, V> Token<S, V> inState(State<S, V> state) {
		return new Token<>(state);
	}
	
	public Token<S, V> moveTo(State<S, V> state) {
		return new Token<>(state);
	}

	public State<S, V> state() {
		return state;
	}
}
