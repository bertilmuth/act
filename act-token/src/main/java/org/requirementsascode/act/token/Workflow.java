package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.function.Step.stepTrigger;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Workflow {
	private final Tokens tokens;
	private final Statemachine<Workflow, Token> statemachine;
	
	public Workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		this.statemachine = statemachine;
		this.tokens = tokens;
	}
	
	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public Tokens tokens(){
		return tokens;
	}

	private static Workflow workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		return new Workflow(statemachine, tokens);
	}
	
	static Workflow workflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		return workflow(statemachineWith(actions, tokenFlows, initialActions), Tokens.tokens(Collections.emptyList()));
	}
	
	public Data<Workflow, Token> replaceToken(Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(tokenBefore, tokenAfter);
		return updatedData(tokensAfter, tokenAfter);
	}
	
	public Data<Workflow, Token> removeToken(Token tokenBefore) {
		Tokens tokensAfter = tokens().removeToken(tokenBefore);
		return updatedData(tokensAfter, null);
	}
	
	public Data<Workflow, Token> moveToken(Data<Workflow, Token> d, Node toNode) {
		return Token.from(d).map(t -> {
			Tokens tokensAfter = tokens().moveToken(t, toNode);
			return updatedData(tokensAfter, t);
		}).orElse(d);
	}
	
	public AfterStep start(ActionData actionData) {
		return nextStep(actionData).nextStep();
	}

	private AfterStep nextStep() {
		return nextStep(stepTrigger);
	}
	
	private AfterStep nextStep(ActionData actionData) {
		requireNonNull(actionData, "actionData must be non-null!");
		Data<Workflow, Token> trigger = actionTrigger(actionData);
		Data<Workflow, Token> outputOfStep = statemachine().actOn(trigger);
		return new AfterStep(statemachine(), outputOfStep);
	}

	private Data<Workflow, Token> actionTrigger(ActionData actionData) {
		DefaultNode defaultNode = DefaultNode.defaultNode(statemachine());
		Data<Workflow, Token> trigger = data(this, token(defaultNode, actionData));
		return trigger;
	}
	
	private Data<Workflow, Token> updatedData(Tokens tokens, Token token) {
		Workflow newWorkflow = Workflow.workflow(this.statemachine(), tokens);
		return data(newWorkflow, token);
	}
	
	@Override
	public String toString() {
		return "Workflow[" + tokens + "]";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<Workflow, Token> statemachineWith(Actions actions, TokenFlows tokenFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		
		Stream<Flow<Workflow, Token>> removeEmptyTokens = Stream.of(RemoveEmptyTokens.removeEmptyTokens());
		Flow[] flowsArray = Stream.concat(
			Stream.concat(initialActions.stream(), tokenFlows.stream()),
			removeEmptyTokens)
		.toArray(Flow[]::new);
		
		Statemachine<Workflow, Token> statemachine = 
				Statemachine.builder()
					.states(actionsArray)
					.transitions()
					.flows(flowsArray)
					.build();
		return statemachine;
	}
	
	public static class AfterStep{
		private final Workflow workflow;
		private final Tokens tokens;
		private final Optional<ActionData> actionOutput;
		
		private AfterStep(Statemachine<Workflow, Token> statemachine, Data<Workflow, Token> outputOfStep) {
			this.workflow = outputOfStep.state();
			this.tokens = workflow.tokens();
			this.actionOutput = outputOfStep.value().flatMap(Token::actionData);
		}
		
		public AfterStep nextStep() {
			return workflow.nextStep();
		}
		
		public Tokens tokens() {
			return tokens;
		}
		
		public Optional<ActionData> actionOutput() {
			return actionOutput;
		}
	}
	
	private Statemachine<Workflow, Token> statemachine() {
		return statemachine;
	}
}
