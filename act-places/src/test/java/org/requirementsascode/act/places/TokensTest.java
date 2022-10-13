package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class TokensTest {
	private static final String TOKEN1 = "Token1";
	private static final String TOKEN2 = "Token2";

	@Test
	void createsEmptyTokens() {
		Tokens<String,String> tokens = Tokens.of();
		
		List<?> tokenList = tokens.stream().toList();
		assertTrue(tokenList.isEmpty());
	}

	@Test
	void createsTokensWithTwoElements() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Tokens<String,String> tokens = Tokens.of(
			Token.create(state1, TOKEN1),
			Token.create(state2, TOKEN2)
		);
		
		List<Token<String,String>> tokenList = tokens.stream().toList();
		assertEquals(TOKEN1, tokenList.get(0).value());
		assertEquals(TOKEN2, tokenList.get(1).value());
	}
}
