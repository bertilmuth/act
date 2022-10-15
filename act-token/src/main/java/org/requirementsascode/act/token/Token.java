package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.requirementsascode.act.statemachine.State;

public class Token<V> {
	private final State<?, V> state;
	private final V value;

	private Token(State<?, V> state, V value) {
		this.state = requireNonNull(state, "state must be non-null!");
		this.value = requireNonNull(value, "value must be non-null!");
	}

	static <S, V> Token<V> token(State<?, V> state, V value) {
		return new Token<>(state, value);
	}
	
	Token<V> moveTo(State<?, V> state) {
		return new Token<>(state, value);
	}

	public State<?, V> state() {
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
		Token<?> other = (Token<?>) obj;
		return Objects.equals(value, other.value);
	}
}
