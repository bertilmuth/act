package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import static org.requirementsascode.act.token.WorkflowState.workflowState;

public class Action {
	public static <V> State<WorkflowState<V>, V> action(String stateName, Behavior<WorkflowState<V>, V, V> actionBehavior) {
		requireNonNull(stateName, "stateName must be non-null!");
		requireNonNull(actionBehavior, "actionBehavior must be non-null!");

		return state(stateName, workflowState -> isAnyTokenInState(workflowState, stateName), 
			d -> act(stateName, d, actionBehavior));
	}
	
	public static <V> boolean isAnyTokenInState(WorkflowState<V> workflowState, String stateName) {
		return workflowState.tokens().inState(stateName).count() != 0;
	}

	private static <V> Data<WorkflowState<V>, V> act(String stateName, Data<WorkflowState<V>, V> data, Behavior<WorkflowState<V>, V, V> actionBehavior) {
			Data<WorkflowState<V>, V> actionOutput = actionBehavior.actOn(data);
			Tokens<V> tokens = actionOutput.state().tokens();
			V firstTokenValue = tokens.firstTokenInState(stateName)
				.map(t -> t.value())
				.orElse(null);
			return data(workflowState(tokens), firstTokenValue);
	}
}
