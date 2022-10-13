package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class TokensTest {
	private static final String TOKEN1 = "Token1";

	@Test
	void createsEmptyTokens() {
		Tokens<String,String> tokens = Tokens.of();
		
		List<?> tokenList = tokens.stream().toList();
		assertTrue(tokenList.isEmpty());
	}

	@Test
	void createsTokensWithOneElement() {
		State<String, String> state1 = state("State1", s -> true);

		Tokens<String,String> tokens = Tokens.of(
			Token.create(state1, TOKEN1)
		);
		
		List<Token<String,String>> tokenList = tokens.stream().toList();
		assertEquals(TOKEN1, tokenList.get(0).value());
	}
}
