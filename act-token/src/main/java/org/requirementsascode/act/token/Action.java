package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import static org.requirementsascode.act.token.Workflow.workflow;

public class Action {
	public static State<Workflow, ActionData> action(String stateName, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		requireNonNull(stateName, "stateName must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");

		Behavior<Workflow, TriggerStep, ActionData> stateBehavior = d -> act(stateName, d, actionBehavior);
		State<Workflow, ActionData> state = state(stateName, workflow -> isAnyTokenInState(workflow, stateName), 
			when(TriggerStep.class, stateBehavior));
		return state;
	}
	
	private static boolean isAnyTokenInState(Workflow workflow, String stateName) {
		return workflow.tokens().inState(stateName).count() != 0;
	}

	private static Data<Workflow, ActionData> act(String stateName, Data<Workflow, TriggerStep> data, Behavior<Workflow, ActionData, ActionData> actionBehavior) {
		Tokens tokens = data.state().tokens();
		ActionData firstTokenValue = tokens.firstTokenInState(stateName)
			.map(t -> t.value())
			.orElse(null);	
		Data<Workflow, ActionData> actionInput = data(workflow(data.state().statemachine(), tokens), firstTokenValue);
		Data<Workflow, ActionData> actionOutput = actionBehavior.actOn(actionInput);
		return actionOutput;
	}
}
