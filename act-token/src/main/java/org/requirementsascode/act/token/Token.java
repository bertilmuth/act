package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.requirementsascode.act.statemachine.State;

public class Token<S, V> {
	private final State<S, V> state;
	private final V value;

	private Token(State<S, V> state, V value) {
		this.state = requireNonNull(state, "state must be non-null!");
		this.value = requireNonNull(value, "value must be non-null!");
	}

	static <S, V> Token<S, V> token(V value, State<S, V> state) {
		return new Token<>(state, value);
	}
	
	Token<S, V> moveTo(State<S, V> state) {
		return new Token<>(state, value);
	}

	public State<S, V> state() {
		return state;
	}
	
	public V value() {
		return value;
	}

	@Override
	public String toString() {
		return "Token [state=" + state + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token<?,?> other = (Token<?,?>) obj;
		return Objects.equals(value, other.value);
	}
}
