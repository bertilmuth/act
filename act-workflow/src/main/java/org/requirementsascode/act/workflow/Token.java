package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import org.requirementsascode.act.core.Data;

public class Token {
	private final Node node;
	private final ActionData actionData;

	Token(Node node, ActionData actionData) {
		this.node = requireNonNull(node, "node must be non-null!");
		this.actionData = actionData;
	}
	
	public static Optional<Token> from(Data<WorkflowState, Token> data) {
		return data.value();
	}
	
	public Node node() {
		return node;
	}
	
	public Optional<ActionData> actionData() {
		return Optional.ofNullable(actionData);
	}
	
	Token moveTo(Node node) {
		return new Token(node, actionData);
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
