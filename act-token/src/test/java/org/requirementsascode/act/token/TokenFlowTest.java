package org.requirementsascode.act.token;

import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class TokenFlowTest {
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";

	@Test
	void test() {
		State<Tokens<String>, String> state1 = state(STATE1, tokens -> tokens.inState(STATE1).count() != 0, 
			this::publishToken);
		
		State<Tokens<String>, String> state2 = state(STATE2, tokens -> tokens.inState(STATE2).count() != 0,
			this::publishToken);
		
		Statemachine<Tokens<String>, String> statemachine =
			Statemachine.builder()
				.states(state1, state2)
				.transitions(
					transition(state1, state2, 
						consumeWith((tokens,value) -> {
							Token<String> token = Token.token(value, state1);
							Tokens<String> newTokens = tokens.moveToken(token, state2);
							return newTokens;
						}))
				)
				.build();
		
		Tokens<String> tokens = Tokens.tokens(
				Token.token("Token1", state1)
		);
	}

	private Data<Tokens<String>, String> publishToken(Data<Tokens<String>, String> data) {
			String firstTokenValue = data.state().inState(STATE1)
				.findFirst()
				.map(t -> t.value())
				.orElse(null);
			return Data.data(data.state(), firstTokenValue);
	}
}
