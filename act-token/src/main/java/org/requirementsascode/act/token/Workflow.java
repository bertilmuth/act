package org.requirementsascode.act.token;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.RemoveTokensWithoutActionData.removeTokensWithoutActionData;
import static org.requirementsascode.act.token.Step.stepTrigger;
import static org.requirementsascode.act.token.Token.token;

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
	private final ActionData actionOutput;
	
	private Workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens, ActionData actionOutput) {
		this.statemachine = statemachine;
		this.tokens = tokens;
		this.actionOutput = actionOutput;
	}
	
	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public AfterStep start(ActionData actionData) {
		return nextStep(actionData).nextStep();
	}
	
	public Optional<ActionData> actionOutput(){
		return Optional.ofNullable(actionOutput);
	}
	
	Tokens tokens(){
		return tokens;
	}
	
	static Workflow initialWorkflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		return createWorkflow(statemachineWith(actions, tokenFlows, initialActions), Tokens.tokens(emptyList()), null);
	}

	private static Workflow createWorkflow(Statemachine<Workflow, Token> statemachine, Tokens tokens, ActionData outputActionData) {
		return new Workflow(statemachine, tokens, outputActionData);
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
		ActionData outputActionData = token != null? token.actionData().orElse(null) : null;
		Workflow workflow = createWorkflow(statemachine(), tokens, outputActionData);
		return data(workflow, token);
	}
	
	private AfterStep nextStep(ActionData actionData) {
		requireNonNull(actionData, "actionData must be non-null!");
		Data<Workflow, Token> trigger = actionTrigger(actionData);
		Workflow updatedWorkflow = statemachine().actOn(trigger).state();
		return new AfterStep(statemachine(), updatedWorkflow);
	}

	private Data<Workflow, Token> actionTrigger(ActionData actionData) {
		return data(this, token(null, actionData));
	}
	
	@Override
	public String toString() {
		return "Workflow[" + tokens + "]";
	}
	
	private Statemachine<Workflow, Token> statemachine() {
		return statemachine;
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
		
		private AfterStep(Statemachine<Workflow, Token> statemachine, Workflow workflow) {
			this.workflow = workflow;
			this.tokens = workflow.tokens();
			this.actionOutput = workflow.actionOutput();
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
