package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import static org.requirementsascode.act.token.Workflow.workflow;

public class Action {
	public static <V> State<Workflow<V>, V> action(String stateName, Behavior<Workflow<V>, V, V> actionBehavior) {
		requireNonNull(stateName, "stateName must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");

		return state(stateName, workflow -> isAnyTokenInState(workflow, stateName), 
			d -> act(stateName, d, actionBehavior));
	}
	
	public static <V> boolean isAnyTokenInState(Workflow<V> workflow, String stateName) {
		return workflow.tokens().inState(stateName).count() != 0;
	}

	private static <V> Data<Workflow<V>, V> act(String stateName, Data<Workflow<V>, V> data, Behavior<Workflow<V>, V, V> actionBehavior) {
			Data<Workflow<V>, V> actionOutput = actionBehavior.actOn(data);
			Tokens<V> tokens = actionOutput.state().tokens();
			V firstTokenValue = tokens.firstTokenInState(stateName)
				.map(t -> t.value())
				.orElse(null);
			return data(workflow(tokens), firstTokenValue);
	}
}
