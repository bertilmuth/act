package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.TriggerStep.triggerStep;

import java.util.List;
import java.util.Optional;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Workflow {
	private final Tokens tokens;
	private final Statemachine<Workflow, Token> statemachine;
	
	public Workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		this.statemachine = requireNonNull(statemachine, "statemachine must be non-null!");
		this.tokens = requireNonNull(tokens, "tokens must be non-null!");
	}

	public static  Workflow workflow(Tokens tokens, Actions actions, List<TokenFlow> tokenFlows){
		Statemachine<Workflow, Token> statemachine = createStatemachine(actions, tokenFlows);
		return workflow(statemachine, tokens);
	}

	static Workflow workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens) {
		return new Workflow(statemachine, tokens);
	}
	
	public Tokens tokens(){
		return tokens;
	}

	Statemachine<Workflow, Token> statemachine() {
		return statemachine;
	}

	@Override
	public String toString() {
		return "Workflow [" + tokens + "]";
	}

	public AfterStep nextStep() {
		Data<Workflow, Token> output = statemachine().actOn(data(this, triggerStep()));
		return new AfterStep(statemachine(), output.state().tokens(), 
			output.value().map(token -> token.actionData()).orElse(null));
	}
	
	public static class AfterStep{
		private final Workflow workflow;
		private final Tokens tokens;
		private final ActionData actionOutput;
		
		private AfterStep(Statemachine<Workflow, Token> statemachine, Tokens tokens, ActionData actionOutput) {
			this.workflow = workflow(statemachine, tokens);
			this.tokens = tokens;
			this.actionOutput = actionOutput;
		}
		
		public AfterStep nextStep() {
			return workflow.nextStep();
		}
		
		public Tokens tokens() {
			return tokens;
		}
		
		public Optional<ActionData> actionOutput() {
			return Optional.ofNullable(actionOutput);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<Workflow, Token> createStatemachine(Actions actions, List<TokenFlow> tokenFlows) {
		Flow[] tokenFlowsArray = tokenFlows.toArray(new Flow[0]);
		State[] actionsArray = actions.asStates().toArray(new State[0]);
		Statemachine<Workflow, Token> statemachine = 
				Statemachine.builder()
					.states(actionsArray)
					.transitions()
					.flows(tokenFlowsArray)
					.build();
		return statemachine;
	}
}
