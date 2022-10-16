package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.TriggerStep.triggerStep;

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

	public Data<Workflow, ActionData> runStep() {
		return statemachine.actOn(data(this, triggerStep()));
	}
}
