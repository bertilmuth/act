package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.RemoveTokensWithoutActionData.removeTokensWithoutActionData;
import static org.requirementsascode.act.token.Step.stepTrigger;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.DefaultNode.defaultNode;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

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
	
	Tokens tokens(){
		return tokens;
	}
	
	public AfterStep start(ActionData actionData) {
		return nextStep(actionData).nextStep();
	}
	
	static Workflow workflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		return workflow(statemachineWith(actions, tokenFlows, initialActions), Tokens.tokens(Collections.emptyList()));
	}

	private static Workflow workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		return new Workflow(statemachine, tokens);
	}
	
	Data<Workflow, Token> replaceToken(Token tokenBefore, Token tokenAfter) {
		Tokens tokensAfter = tokens().replaceToken(tokenBefore, tokenAfter);
		return updatedData(tokensAfter, tokenAfter);
	}
	
	Data<Workflow, Token> removeToken(Token tokenBefore) {
		Tokens tokensAfter = tokens().removeToken(tokenBefore);
		return updatedData(tokensAfter, null);
	}
	
	Data<Workflow, Token> moveToken(Data<Workflow, Token> d, Node toNode) {
		return Token.from(d).map(t -> {
			Tokens tokensAfterMove = tokens().moveToken(t, toNode);
			return updatedData(tokensAfterMove, t);
		}).orElse(d);
	}
	
	private Data<Workflow, Token> updatedData(Tokens tokens, Token token) {
		return data(workflow(statemachine(), tokens), token);
	}
	
	private AfterStep nextStep(ActionData actionData) {
		requireNonNull(actionData, "actionData must be non-null!");
		Data<Workflow, Token> trigger = actionTrigger(actionData);
		Data<Workflow, Token> outputOfStep = statemachine().actOn(trigger);
		return new AfterStep(statemachine(), outputOfStep);
	}

	private Data<Workflow, Token> actionTrigger(ActionData actionData) {
		DefaultNode defaultNode = defaultNode(statemachine());
		Data<Workflow, Token> trigger = data(this, token(defaultNode, actionData));
		return trigger;
	}
	
	@Override
	public String toString() {
		return "Workflow[" + tokens + "]";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<Workflow, Token> statemachineWith(Actions actions, TokenFlows tokenFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		
		Flow[] flowsArray = Stream.concat(
			Stream.concat(initialActions.stream(), tokenFlows.stream()),
			Stream.of(removeTokensWithoutActionData()))
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
			return workflow.nextStep(stepTrigger);
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

class RemoveTokensWithoutActionData implements Flow<Workflow, Token> {
	public static RemoveTokensWithoutActionData removeTokensWithoutActionData() {
		return new RemoveTokensWithoutActionData();
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(owningStatemachine.definedState(), owningStatemachine.definedState(), 
			whenInCase(Token.class, this::hasNoActionData, this::removeToken));
	}

	private boolean hasNoActionData(Data<Workflow, Token> d) {
		return d.value().map(t -> !t.actionData().isPresent()).orElse(false);
	}

	private Data<Workflow, Token> removeToken(Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, Token> resultWorkflowWithRemovedToken = workflow.removeToken(token);
		return resultWorkflowWithRemovedToken;
	}
}
