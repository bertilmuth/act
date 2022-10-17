package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.requirementsascode.act.statemachine.State;

public class Token {
	private final State<?, Token> state;
	private final ActionData value;

	private Token(State<?, Token> state, ActionData value) {
		//this.state = requireNonNull(state, "state must be non-null!");
		this.state = state;
		this.value = requireNonNull(value, "value must be non-null!");
	}

	static Token token(State<?, Token> state, ActionData value) {
		return new Token(state, value);
	}
	
	Token moveTo(State<?, Token> state) {
		return new Token(state, value);
	}

	public State<?, Token> state() {
		return state;
	}
	
	public ActionData value() {
		return value;
	}

	@Override
	public String toString() {
		return "Token [state=" + state + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		return Objects.equals(state, other.state) && Objects.equals(value, other.value);
	}
}
