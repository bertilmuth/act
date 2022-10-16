package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.TriggerStep.triggerStep;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;

public class Workflow {
	private final Tokens tokens;
	
	private Workflow(Tokens tokens) {
		this.tokens = tokens;
	}

	public static  Workflow workflow(Tokens tokens){
		return new Workflow(tokens);
	}
	
	public Tokens tokens(){
		return tokens;
	}

	@Override
	public String toString() {
		return "Workflow [" + tokens + "]";
	}

	public Data<Workflow, ActionData> runStep(Statemachine<Workflow, ActionData> statemachine) {
		return statemachine.actOn(data(this, triggerStep()));
	}
}
