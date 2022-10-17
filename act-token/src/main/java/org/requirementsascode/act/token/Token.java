package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.requirementsascode.act.statemachine.State;

public class Token {
	private final State<?, Token> state;
	private final ActionData actionData;

	private Token(State<?, Token> state, ActionData actionData) {
		this.state = state;
		this.actionData = requireNonNull(actionData, "actionData must be non-null!");
	}

	static Token token(State<?, Token> state, ActionData actionData) {
		return new Token(state, actionData);
	}
	
	Token moveTo(State<?, Token> state) {
		return new Token(state, actionData);
	}

	public State<?, Token> state() {
		return state;
	}
	
	public ActionData actionData() {
		return actionData;
	}

	@Override
	public String toString() {
		return "Token [state=" + state + ", value=" + actionData + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, actionData);
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
		return Objects.equals(state, other.state) && Objects.equals(actionData, other.actionData);
	}
}
