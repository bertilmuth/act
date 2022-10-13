package org.requirementsascode.act.places;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.State;

public class Token<S, V> {
	private final State<S, V> state;
	private final V value;

	private Token(State<S, V> state, V value) {
		this.state = requireNonNull(state, "state must be non-null!");
		this.value = requireNonNull(value, "value must be non-null!");
	}

	public static <S, V> Token<S, V> create(State<S, V> state, V value) {
		return new Token<>(state, value);
	}
	
	public Token<S, V> moveTo(State<S, V> state) {
		return new Token<>(state, value);
	}

	public State<S, V> state() {
		return state;
	}
	
	public V value() {
		return value;
	}
}