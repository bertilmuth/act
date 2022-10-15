package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import static org.requirementsascode.act.token.Workflow.workflow;

public class Action {
	public static <V> State<Workflow<ActionData>, ActionData> action(String stateName, Behavior<Workflow<ActionData>, ActionData, ActionData> actionBehavior) {
		requireNonNull(stateName, "stateName must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");

		Behavior<Workflow<ActionData>, RunStep, ActionData> stateBehavior = d -> act(stateName, d, actionBehavior);
		State<Workflow<ActionData>, ActionData> state = state(stateName, workflow -> isAnyTokenInState(workflow, stateName), 
			when(RunStep.class, stateBehavior));
		return state;
	}
	
	public static <V> boolean isAnyTokenInState(Workflow<V> workflow, String stateName) {
		return workflow.tokens().inState(stateName).count() != 0;
	}

	private static <V> Data<Workflow<ActionData>, ActionData> act(String stateName, Data<Workflow<ActionData>, RunStep> data, Behavior<Workflow<ActionData>, ActionData, ActionData> actionBehavior) {
		Tokens<ActionData> tokens = data.state().tokens();
		ActionData firstTokenValue = tokens.firstTokenInState(stateName)
			.map(t -> t.value())
			.orElse(null);	
		Data<Workflow<ActionData>, ActionData> actionInput = data(workflow(tokens), firstTokenValue);
		Data<Workflow<ActionData>, ActionData> actionOutput = actionBehavior.actOn(actionInput);
		return actionOutput;
	}
}
