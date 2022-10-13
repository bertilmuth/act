package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class TokenTest {
	private static final String TOKEN1 = "Token1";

	@Test
	void createsTokenInState() {
		State<String, String> state1 = state("State1", s -> true);
		Token<String, String> token = Token.create(state1, TOKEN1);
		assertEquals(state1, token.state());
		assertEquals(TOKEN1, token.value());
	}

	@Test
	void movesTokenToAnotherState() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		Token<String, String> tokenInState1 = Token.create(state1, TOKEN1);
		Token<String, String> tokenInState2 = tokenInState1.moveTo(state2);
		
		assertEquals(state1, tokenInState1.state());
		assertEquals(state2, tokenInState2.state());
		assertEquals(TOKEN1, tokenInState2.value());
	}
}		

