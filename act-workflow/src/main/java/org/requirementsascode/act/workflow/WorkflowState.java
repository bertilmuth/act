package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
import static org.requirementsascode.act.core.Data.data;

import java.util.Optional;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;

public class WorkflowState {
	private final Statemachine<WorkflowState, Token> statemachine;
	private final Tokens tokens;
	private final ActionData actionOutput;
	
	private WorkflowState(Statemachine<WorkflowState, Token> statemachine, Tokens tokens, ActionData actionOutput) {
		this.statemachine = statemachine;
		this.tokens = tokens;
		this.actionOutput = actionOutput;
	}
	
	static WorkflowState intialWorkflowState(Statemachine<WorkflowState, Token> statemachine) {
		return new WorkflowState(statemachine, new Tokens(emptyList()), null);
	}
	
	Data<WorkflowState, Token> replaceToken(Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(tokenBefore, tokenAfter);
		return updateTokens(tokensAfter, tokenAfter);
	}
	
	Data<WorkflowState, Token> moveToken(Data<WorkflowState, Token> d, Node toNode) {
		return Token.from(d).map(t -> {
			Tokens tokensAfterMove = tokens().moveToken(t, toNode);
			return updateTokens(tokensAfterMove, t);
		}).orElse(d);
	}
	
	Data<WorkflowState, Token> removeToken(Token tokenBefore) {
		Tokens tokensAfter = tokens().removeToken(tokenBefore);
		return updateTokens(tokensAfter, null);
	}
	
	private Data<WorkflowState, Token> updateTokens(Tokens tokens, Token token) {
		ActionData actionOutput = token != null? token.actionData().orElse(null) : null;
		WorkflowState newWorkflowState = new WorkflowState(statemachine, tokens, actionOutput);
		return data(newWorkflowState, token);
	}

	public Tokens tokens() {
		return tokens;
	}

	Optional<ActionData> actionOutput() {
		return Optional.ofNullable(actionOutput);
	}
}