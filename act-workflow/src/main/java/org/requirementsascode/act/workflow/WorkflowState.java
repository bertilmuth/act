package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
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
		return new WorkflowState(statemachine, new Tokens(emptyList()), null);
	}
	
	public Tokens tokens() {
		return tokens;
	}
	
	public Stream<Token> tokensIn(Node node) {
		return tokens().stream().filter(token -> token.node().equals(node));
	}
	
	public Optional<Token> firstTokenIn(Node node) {
		return tokensIn(node).findFirst();
	}
	
	public boolean areTokensIn(Node node){
		return firstTokenIn(node).isPresent();
	}
	
	Data<WorkflowState, Token> replaceToken(Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(tokenBefore, tokenAfter);
		return updateTokens(tokensAfter, tokenAfter);
	}
	
	Data<WorkflowState, Token> moveToken(Data<WorkflowState, Token> inputDataWithToken, Node toNode) {
		Token tokenBefore = Token.from(inputDataWithToken);
		Token tokenAfter = tokenBefore.moveTo(toNode);
		Tokens tokensAfterMove = tokens().replaceToken(tokenBefore, tokenAfter);
		return updateTokens(tokensAfterMove, tokenAfter);
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