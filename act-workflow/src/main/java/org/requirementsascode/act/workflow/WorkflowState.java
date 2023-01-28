package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyMap;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;

public class WorkflowState {
	private final Tokens tokens;
	
	WorkflowState(Tokens tokens) {
		this.tokens = tokens;
	}
	
	static WorkflowState initial() {
		return new WorkflowState(new Tokens(emptyMap()));
	}
	
	public Tokens tokens() {
		return tokens;
	}
	
	public Stream<Token> tokensIn(Port<?> port) {
		return tokens().tokensIn(port);
	}
	
	public Optional<Token> firstTokenIn(Port<?> port) {
		return tokensIn(port).findFirst();
	}
	
	public boolean areTokensIn(Port<?> port){
		return firstTokenIn(port).isPresent();
	}
	
	Data<WorkflowState, Token> addToken(Port<?> port, Token token) {
		Tokens tokensAfter = tokens().addToken(port, token);
		return updateTokens(tokensAfter, token);
	}
	
	Data<WorkflowState, Token> removeToken(Port<?> port, Token token) {
		Tokens tokensAfter = tokens().removeToken(port, token);
		return updateTokens(tokensAfter, token);
	}
	
	Data<WorkflowState, Token> replaceToken(Port<?> port, Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(port, tokenBefore, tokenAfter);
		return updateTokens(tokensAfter, tokenAfter);
	}

	private Data<WorkflowState, Token> updateTokens(Tokens tokens, Token token) {
		WorkflowState newWorkflowState = new WorkflowState(tokens);
		return data(newWorkflowState, token);
	}

	@Override
	public String toString() {
		return "WorkflowState [" + tokens + "]";
	}
}