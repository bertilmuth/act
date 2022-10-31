package org.requirementsascode.act.token;

import java.util.Objects;
import java.util.Optional;

import org.requirementsascode.act.core.Data;

public class Token {
	private final Node node;
	private final ActionData actionData;

	private Token(Node node, ActionData actionData) {
		this.node = node;
		this.actionData = actionData;
	}

	public static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	public static Optional<Token> from(Data<Workflow, Token> data) {
		return data.value();
	}
	
	Token moveTo(Node node) {
		return new Token(node, actionData);
	}

	public Node node() {
		return node;
	}
	
	public Optional<ActionData> actionData() {
		return Optional.ofNullable(actionData);
	}

	@Override
	public String toString() {
		return "Token[node=" + node + ", actionData=" + actionData + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(node, actionData);
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
		return Objects.equals(node, other.node) && Objects.equals(actionData, other.actionData);
	}
}
