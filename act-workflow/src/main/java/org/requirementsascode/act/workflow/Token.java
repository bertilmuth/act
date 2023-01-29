package org.requirementsascode.act.workflow;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.requirementsascode.act.core.Data;

public class Token {
	private final UUID uuid;
	private final ActionData actionData;

	Token(ActionData actionData) {
		this(UUID.randomUUID(), actionData);
	}
	
	Token(UUID uuid, ActionData actionData) {
		this.uuid = uuid;
		this.actionData = actionData;
	}
	
	public static Token empty() {
		return new Token(null);
	}
	
	public static Token from(Data<WorkflowState, Token> data) {
		return data.value().orElse(null);
	}
	
	public Optional<ActionData> actionData() {
		return Optional.ofNullable(actionData);
	}
	
	public Token replaceActionData(ActionData outputActionData) {
		return new Token(uuid, outputActionData);
	}

	@Override
	public String toString() {
		return "Token[" + actionData + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
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
		return Objects.equals(uuid, other.uuid);
	}
}
