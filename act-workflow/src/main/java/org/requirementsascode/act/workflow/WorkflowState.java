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
	private final ActionData actionOutput;
	
	WorkflowState(Workflow workflow, Tokens tokens, ActionData actionOutput) {
		this.workflow = workflow;
		this.statemachine = workflow.statemachine();
		this.tokens = tokens;
		this.actionOutput = actionOutput;
	}
	
	static WorkflowState createInitialWorkflowState(Workflow workflow, Statemachine<WorkflowState, Token> statemachine) {
		return new WorkflowState(workflow, new Tokens(emptyMap()), null);
	}
	
	public Tokens tokens() {
		return tokens;
	}
	
	Workflow workflow() {
		return workflow;
	}
	
	public Stream<Token> tokensIn(Node node) {
		return tokens().tokensIn(node);
	}
	
	public Optional<Token> firstTokenIn(Node node) {
		return tokensIn(node).findFirst();
	}
	
	public boolean areTokensIn(Node node){
		boolean result = firstTokenIn(node).isPresent();
		return result;
	}
	
	public Data<WorkflowState, Token> addToken(Node node, Token token) {
		Tokens tokensAfter = tokens().addToken(node, token);
		return updateTokens(tokensAfter, token);
	}
	
	public Data<WorkflowState, Token> removeToken(Node node, Token token) {
		Tokens tokensAfter = tokens().removeToken(node, token);
		return updateTokens(tokensAfter, token);
	}
	
	WorkflowState updateActionOutput(ActionData newActionOutput) {
		return new WorkflowState(workflow, tokens, newActionOutput);
	}
	
	Data<WorkflowState, Token> replaceToken(Node node, Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(node, tokenBefore, tokenAfter);
		return updateTokens(tokensAfter, tokenAfter);
	}

	private Data<WorkflowState, Token> updateTokens(Tokens tokens, Token token) {
		ActionData actionOutput = token != null? token.actionData().orElse(null) : null;
		WorkflowState newWorkflowState = new WorkflowState(workflow, tokens, actionOutput);
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