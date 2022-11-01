package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;

import java.util.Optional;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;

public class WorkflowState {
	private final Statemachine<Workflow, Token> statemachine;
	private final Tokens tokens;
	private final ActionData actionOutput;
	
	WorkflowState(Statemachine<Workflow, Token> statemachine, Tokens tokens, ActionData actionOutput) {
		this.statemachine = statemachine;
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
		ActionData outputActionData = token != null? token.actionData().orElse(null) : null;
		WorkflowState newWorkflowState = new WorkflowState(statemachine, tokens, outputActionData);
		Workflow updatedWorkflow = new Workflow(statemachine, newWorkflowState);
		return data(updatedWorkflow, token);
	}

	public Tokens tokens() {
		return tokens;
	}

	public Optional<ActionData> actionOutput() {
		return Optional.ofNullable(actionOutput);
	}
}