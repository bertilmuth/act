package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action {
	public static <V> State<Tokens<V>, V> action(String stateName, Behavior<Tokens<V>, V, V> actionBehavior) {
		return state(stateName, tokens -> isAnyTokenInState(tokens, stateName), 
			d -> act(stateName, d, actionBehavior));
	}
	
	public static <V> boolean isAnyTokenInState(Tokens<V> tokens, String stateName) {
		return tokens.inState(stateName).count() != 0;
	}

	private static <V> Data<Tokens<V>, V> act(String stateName, Data<Tokens<V>, V> data, Behavior<Tokens<V>, V, V> actionBehavior) {
			Data<Tokens<V>, V> actionOutput = actionBehavior.actOn(data);
			Tokens<V> tokens = actionOutput.state();
			V firstTokenValue = tokens.firstTokenInState(stateName)
				.map(t -> t.value())
				.orElse(null);
			return data(tokens, firstTokenValue);
	}
}
