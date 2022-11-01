package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;

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
		return updatedData(tokensAfter, tokenAfter);
	}
	
	Data<Workflow, Token> moveToken(Data<Workflow, Token> d, Node toNode) {
		return Token.from(d).map(t -> {
			Tokens tokensAfterMove = tokens().moveToken(t, toNode);
			return updatedData(tokensAfterMove, t);
		}).orElse(d);
	}
	
	Data<Workflow, Token> removeToken(Token tokenBefore) {
		Tokens tokensAfter = tokens().removeToken(tokenBefore);
		return updatedData(tokensAfter, null);
	}
	
	private Data<Workflow, Token> updatedData(Tokens tokens, Token token) {
		ActionData outputActionData = token != null? token.actionData().orElse(null) : null;
		Workflow updatedWorkflow = Workflow.createWorkflow(workflow.statemachine(), tokens, outputActionData);
		return data(updatedWorkflow, token);
	}

	public Tokens tokens() {
		return tokens;
	}

	public Optional<ActionData> actionOutput() {
		return Optional.ofNullable(actionOutput);
	}
}