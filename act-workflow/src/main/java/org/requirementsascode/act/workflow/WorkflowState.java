package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyMap;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;

public class WorkflowState {
	private final Workflow workflow;
	private final Statemachine<WorkflowState, Token> statemachine;
	private final Tokens tokens;
	
	WorkflowState(Workflow workflow, Tokens tokens) {
		this.workflow = workflow;
		this.statemachine = workflow.statemachine();
		this.tokens = tokens;
	}
	
	static WorkflowState createInitialWorkflowState(Workflow workflow, Statemachine<WorkflowState, Token> statemachine) {
		return new WorkflowState(workflow, new Tokens(emptyMap()));
	}
	
	public Tokens tokens() {
		return tokens;
	}
	
	Workflow workflow() {
		return workflow;
	}
	
	Stream<Token> tokensIn(Port<?> port) {
		return tokens().tokensIn(port);
	}
	
	Optional<Token> firstTokenIn(Port<?> port) {
		return tokensIn(port).findFirst();
	}
	
	boolean areTokensIn(Port<?> port){
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
		WorkflowState newWorkflowState = new WorkflowState(workflow(), tokens);
		return data(newWorkflowState, token);
	}
	
	Statemachine<WorkflowState, Token> statemachine() {
		return statemachine;
	}

	@Override
	public String toString() {
		return "WorkflowState [" + tokens + "]";
	}
}