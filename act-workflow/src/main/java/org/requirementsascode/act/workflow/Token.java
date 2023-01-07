package org.requirementsascode.act.workflow;

import java.util.Objects;
import java.util.Optional;

import org.requirementsascode.act.core.Data;

public class Token {
	private final ActionData actionData;

	Token(ActionData actionData) {
		this.actionData = actionData;
	}
	
	public static Token from(Data<WorkflowState, Token> data) {
		return data.value().orElse(null);
	}
	
	public Optional<ActionData> actionData() {
		return Optional.ofNullable(actionData);
	}
	
	Token replaceActionData(ActionData outputActionData) {
		return new Token(outputActionData);
	}

	@Override
	public String toString() {
		return "Token[" + actionData + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(actionData);
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
		return Objects.equals(actionData, other.actionData);
	}
}
