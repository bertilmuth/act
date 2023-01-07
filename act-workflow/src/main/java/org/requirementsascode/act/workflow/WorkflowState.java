package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyMap;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;

public class WorkflowState {
	private final Statemachine<WorkflowState, Token> statemachine;
	private final Tokens tokens;
	private final ActionData actionOutput;
	
	WorkflowState(Statemachine<WorkflowState, Token> statemachine, Tokens tokens, ActionData actionOutput) {
		this.statemachine = statemachine;
		this.tokens = tokens;
		this.actionOutput = actionOutput;
	}
	
	static WorkflowState intialWorkflowState(Statemachine<WorkflowState, Token> statemachine) {
		return new WorkflowState(statemachine, new Tokens(emptyMap()), null);
	}
	
	public Tokens tokens() {
		return tokens;
	}
	
	public Stream<Token> tokensIn(Node node) {
		return tokens().tokensIn(node);
	}
	
	public Optional<Token> firstTokenIn(Node node) {
		return tokensIn(node).findFirst();
	}
	
	public boolean areTokensIn(Node node){
		return firstTokenIn(node).isPresent();
	}
	
	Data<WorkflowState, Token> replaceToken(Node beforeNode, Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(beforeNode, tokenBefore, tokenAfter);
		return updateTokens(tokensAfter, tokenAfter);
	}
	
	Data<WorkflowState, Token> moveToken(Token tokenToMove, Node fromNode, Node toNode) {
		Token movedToken = tokenToMove.moveTo(toNode);
		Tokens tokensAfter = tokens().moveToken(tokenToMove, fromNode, toNode);
		return updateTokens(tokensAfter, movedToken);
	}
	
	private Data<WorkflowState, Token> updateTokens(Tokens tokens, Token token) {
		ActionData actionOutput = token != null? token.actionData().orElse(null) : null;
		WorkflowState newWorkflowState = new WorkflowState(statemachine, tokens, actionOutput);
		return data(newWorkflowState, token);
	}

	Optional<ActionData> actionOutput() {
		return Optional.ofNullable(actionOutput);
	}
	
	Statemachine<WorkflowState, Token> statemachine() {
		return statemachine;
	}

	@Override
	public String toString() {
		return "WorkflowState [tokens=" + tokens + ", actionOutput=" + actionOutput + "]";
	}
}