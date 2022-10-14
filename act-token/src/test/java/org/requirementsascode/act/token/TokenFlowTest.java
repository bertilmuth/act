package org.requirementsascode.act.token;

import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class TokenFlowTest {
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";

	@Test
	void test() {
		State<Tokens<Value>, Value> state1 = state(STATE1, tokens -> tokens.inState(STATE1).count() != 0, 
			d -> publishToken(STATE1, d));
		
		State<Tokens<Value>, Value> state2 = state(STATE2, tokens -> tokens.inState(STATE2).count() != 0,
			d -> publishToken(STATE2, d));
		
		Statemachine<Tokens<Value>, Value> statemachine =
			Statemachine.builder()
				.states(state1, state2)
				.transitions(
					transition(state1, state2, 
						consumeWith((tokens,value) -> {
							Token<Value> token = Token.token(value, state1);
							Tokens<Value> newTokens = tokens.moveToken(token, state2);
							return newTokens;
						}))
				)
				.build();
		
		Tokens<Value> tokens = Tokens.tokens(
				Token.token(new Value(), state1)
		);
		
		statemachine.actOn(Data.data(tokens, new Tick()));
	}

	private <V> Data<Tokens<V>, V> publishToken(String stateName, Data<Tokens<V>, V> data) {
			V firstTokenValue = data.state().inState(stateName)
				.findFirst()
				.map(t -> t.value())
				.orElse(null);
			return Data.data(data.state(), firstTokenValue);
	}
	
	private static class Value{};
	private static class Tick extends Value{};
}
