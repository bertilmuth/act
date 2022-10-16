package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.TriggerStep.triggerStep;

import java.util.Optional;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;

public class Workflow {
	private final Tokens tokens;
	private final Statemachine<Workflow, ActionData> statemachine;
	
	private Workflow(Tokens tokens, Statemachine<Workflow, ActionData> statemachine) {
		this.tokens = requireNonNull(tokens, "tokens must be non-null!");
		this.statemachine = requireNonNull(statemachine, "statemachine must be non-null!");
	}

	public static  Workflow workflow(Statemachine<Workflow, ActionData> statemachine, Tokens tokens){
		return new Workflow(tokens, statemachine);
	}
	
	public Tokens tokens(){
		return tokens;
	}

	public Statemachine<Workflow, ActionData> statemachine() {
		return statemachine;
	}

	@Override
	public String toString() {
		return "Workflow [" + tokens + "]";
	}

	public WorkflowStep runStep() {
		Data<Workflow, ActionData> output = statemachine.actOn(data(this, triggerStep()));
		return new WorkflowStep(statemachine(), output.state().tokens(), output.value().orElse(null));
	}
	
	public static class WorkflowStep{
		private final Workflow workflow;
		private final Tokens tokens;
		private final ActionData actionOutput;
		
		private WorkflowStep(Statemachine<Workflow, ActionData> statemachine, Tokens tokens, ActionData actionOutput) {
			this.workflow = workflow(statemachine, tokens);
			this.tokens = tokens;
			this.actionOutput = actionOutput;
		}
		
		public WorkflowStep runStep() {
			return workflow.runStep();
		}
		
		public Tokens tokens() {
			return tokens;
		}
		
		public Optional<ActionData> actionOutput() {
			return Optional.ofNullable(actionOutput);
		}
	}
}
