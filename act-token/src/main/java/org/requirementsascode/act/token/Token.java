package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

public class Token {
	private final Node node;
	private final ActionData actionData;

	private Token(Node node, ActionData actionData) {
		this.node = node;
		this.actionData = requireNonNull(actionData, "actionData must be non-null!");
	}

	static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	Token moveTo(Node node) {
		return new Token(node, actionData);
	}
	
	boolean isTriggerOfNextStep() {
		return actionData() instanceof TriggerNextStep;
	}

	public Node node() {
		return node;
	}
	
	public ActionData actionData() {
		return actionData;
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
