package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.*;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class TokenTest {
	@Test
	void createsTokenInState() {
		State<String, String> state1 = state("State1", s -> true);
		Token<String, String> token = Token.inState(state1);
		assertEquals(state1, token.state());
	}

	@Test
	void movesTokenToAnotherState() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		Token<String, String> tokenInState1 = Token.inState(state1);
		Token<String, String> tokenInState2 = tokenInState1.moveTo(state2);
		
		assertEquals(state1, tokenInState1.state());
		assertEquals(state2, tokenInState2.state());
	}
}		

