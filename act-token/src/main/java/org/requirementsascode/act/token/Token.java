package org.requirementsascode.act.token;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.Step.Proceed;

public class Token {
	private final Node node;
	private final ActionData actionData;

	private Token(Node node, ActionData actionData) {
		this.node = requireNonNull(node, "node must be non-null!");
		this.actionData = actionData;
	}
	
	public static Optional<Token> from(Data<Workflow, Token> data) {
		return data.value();
	}
	
	public static Token token(Node node, ActionData actionData) {
		return new Token(node, actionData);
	}
	
	public Node node() {
		return node;
	}
	
	public Optional<ActionData> actionData() {
		return Optional.ofNullable(actionData);
	}
	
	public static boolean isForProceeding(Data<Workflow, Token> inputData) {
		return Token.from(inputData).map(Token::containsProceed).orElse(false);
	}

	private static boolean containsProceed(Token token) {
		return token.actionData().map(ad -> ad instanceof Proceed).orElse(false);
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
