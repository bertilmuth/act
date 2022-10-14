package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action {
	public static <V> State<Tokens<V>, V> action(String stateName) {
		return state(stateName, tokens -> isAnyTokenInState(tokens, stateName), 
			d -> publishToken(stateName, d));
	}
	
	public static <V> boolean isAnyTokenInState(Tokens<V> tokens, String stateName) {
		return tokens.inState(stateName).count() != 0;
	}

	private static <V> Data<Tokens<V>, V> publishToken(String stateName, Data<Tokens<V>, V> data) {
			Tokens<V> tokensBefore = data.state();
			V firstTokenValue = tokensBefore.firstTokenInState(stateName)
				.map(t -> t.value())
				.orElse(null);
			return data(tokensBefore, firstTokenValue);
	}
}
