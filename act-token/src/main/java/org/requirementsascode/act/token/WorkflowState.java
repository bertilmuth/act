package org.requirementsascode.act.token;

import java.util.Optional;

import org.requirementsascode.act.core.Data;

public class WorkflowState {
	private final Workflow workflow;
	private final Tokens tokens;
	private final ActionData actionOutput;
	
	WorkflowState(Workflow workflow, Tokens tokens, ActionData actionOutput) {
		this.workflow = workflow;
		this.tokens = tokens;
		this.actionOutput = actionOutput;
	}
	
	Data<Workflow, Token> replaceToken(Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(tokenBefore, tokenAfter);
		return updateTokens(tokensAfter, tokenAfter);
	}
	
	Data<Workflow, Token> moveToken(Data<Workflow, Token> d, Node toNode) {
		return Token.from(d).map(t -> {
			Tokens tokensAfterMove = tokens().moveToken(t, toNode);
			return updateTokens(tokensAfterMove, t);
		}).orElse(d);
	}
	
	Data<Workflow, Token> removeToken(Token tokenBefore) {
		Tokens tokensAfter = tokens().removeToken(tokenBefore);
		return updateTokens(tokensAfter, null);
	}
	
	private Data<Workflow, Token> updateTokens(Tokens tokens, Token token) {
		return workflow.updateTokens(tokens, token);
	}

	public Tokens tokens() {
		return tokens;
	}

	public Optional<ActionData> actionOutput() {
		return Optional.ofNullable(actionOutput);
	}
}