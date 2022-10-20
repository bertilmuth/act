package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TriggerSystemFunction.triggerNextStep;

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
	
	public Tokens tokens(){
		return tokens;
	}
	
	static Workflow workflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		return workflow(statemachineWith(actions, tokenFlows, initialActions), Tokens.tokens(Collections.emptyList()));
	}

	static Workflow workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		return new Workflow(statemachine, tokens);
	}

	AfterStep nextStep() {
		return nextStep(triggerNextStep());
	}
	
	public AfterStep nextStep(ActionData actionData) {
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
	
	@Override
	public String toString() {
		return "Workflow[" + tokens + "]";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<Workflow, Token> statemachineWith(Actions actions, TokenFlows tokenFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		Flow[] flowsArray = Stream.concat(initialActions.stream(), tokenFlows.stream()).toArray(Flow[]::new);
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
			this.actionOutput = outputOfStep.value().map(Token::actionData);
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
	
	Statemachine<Workflow, Token> statemachine() {
		return statemachine;
	}
}
