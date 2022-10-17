package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.TriggerNextStep.triggerNextStep;

import java.util.Optional;

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

	public static  Workflow workflow(Tokens tokens, Actions actions, TokenFlows tokenFlows){
		requireNonNull(tokens, "tokens must be non-null!");
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");

		return workflow(statemachineWith(actions, tokenFlows), tokens);
	}

	static Workflow workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		return new Workflow(statemachine, tokens);
	}
	
	public AfterStep nextStep() {
		Data<Workflow, Token> outputOfStep = statemachine().actOn(triggerNextStepOfWorkflow());
		return new AfterStep(statemachine(), outputOfStep);
	}
	
	public Tokens tokens(){
		return tokens;
	}
	
	@Override
	public String toString() {
		return "Workflow [" + tokens + "]";
	}
	
	public static class AfterStep{
		private final Workflow workflow;
		private final Tokens tokens;
		private final Optional<ActionData> actionOutput;
		
		private AfterStep(Statemachine<Workflow, Token> statemachine, Data<Workflow, Token> outputOfStep) {
			this.tokens = outputOfStep.state().tokens();
			this.workflow = workflow(statemachine, tokens);
			this.actionOutput = outputOfStep.value().map(token -> token.actionData());
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
	
	private Data<Workflow, Token> triggerNextStepOfWorkflow() {
		return data(this, triggerNextStep());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<Workflow, Token> statemachineWith(Actions actions, TokenFlows tokenFlows) {
		Flow[] tokenFlowsArray = tokenFlows.stream().toArray(Flow[]::new);
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		Statemachine<Workflow, Token> statemachine = 
				Statemachine.builder()
					.states(actionsArray)
					.transitions()
					.flows(tokenFlowsArray)
					.build();
		return statemachine;
	}
}
