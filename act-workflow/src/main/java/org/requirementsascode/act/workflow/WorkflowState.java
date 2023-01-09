package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyMap;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transitions;

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
	
	boolean areTokensInNodesBefore(Node node) {
		return nodesBefore(node, workflow).stream()
			.map(this::areTokensIn)
			.reduce(true, (a,b) -> a && b);
	}
	
	List<Node> nodesBefore(Node node, Workflow workflow) {
		State<WorkflowState, Token> nodeState = node.asState();
		Transitions<WorkflowState, Token> incomingTransitions = statemachine.incomingTransitions(nodeState);
		List<State<WorkflowState, Token>> statesBefore = statesBefore(incomingTransitions);
		List<Node> nodesBefore = workflow.nodes().stream()
			.filter(n -> statesBefore.contains(n.asState()))
			.collect(Collectors.toList());
			
		return nodesBefore;
	}
	
	private List<State<WorkflowState, Token>> statesBefore(Transitions<WorkflowState, Token> incomingTransitions) {
		return incomingTransitions.stream()
			.map(t -> t.asTransition(statemachine).fromState())
			.collect(Collectors.toList());
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