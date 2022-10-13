package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.stream.Stream;

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
	
	@Test
	void findsNoTokensForStateWhereNoTokensHaveBeenPlaced() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Tokens<String,String> tokens = Tokens.of(
			Token.create(state1, TOKEN1)
		);
		
		Stream<Token<String,String>> foundTokens = tokens.findByState(state2);
		List<Token<String,String>> tokenList = foundTokens.toList();
		assertTrue(tokenList.isEmpty());
	}
	
	@Test
	void findsTokenForStateWhereOneTokenHasBeenPlaced() {
		State<String, String> state1 = state("State1", s -> true);
		
		Tokens<String,String> tokens = Tokens.of(
			Token.create(state1, TOKEN1)
		);
		
		Stream<Token<String,String>> foundTokens = tokens.findByState(state1);
		List<Token<String,String>> tokenList = foundTokens.toList();
		assertEquals(TOKEN1, tokenList.get(0).value());
	}
	
	@Test
	void findsTokensForStateWhereTwoTokensHaveBeenPlaced() {
		State<String, String> state1 = state("State1", s -> true);
		
		Tokens<String,String> tokens = Tokens.of(
			Token.create(state1, TOKEN1),
			Token.create(state1, TOKEN2)
		);
		
		Stream<Token<String,String>> foundTokens = tokens.findByState(state1);
		List<Token<String,String>> tokenList = foundTokens.toList();
		assertEquals(TOKEN1, tokenList.get(0).value());
		assertEquals(TOKEN2, tokenList.get(1).value());
	}
	
	@Test
	void findsTokensForStateWhereTwoTokensHaveBeenPlaced_whenTheresAnotherState() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		
		Tokens<String,String> tokens = Tokens.of(
			Token.create(state1, TOKEN1),
			Token.create(state2, TOKEN2),
			Token.create(state2, TOKEN1)
		);
		
		Stream<Token<String,String>> foundTokens = tokens.findByState(state2);
		List<Token<String,String>> tokenList = foundTokens.toList();
		assertEquals(TOKEN2, tokenList.get(0).value());
		assertEquals(TOKEN1, tokenList.get(1).value());
	}
	
	@Test
	void doesntMoveTokenThatIsntOneOfTheTokens() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		
		Token<String, String> token1 = Token.create(state1, TOKEN1);
		Token<String, String> token2 = Token.create(state2, TOKEN2);
		Tokens<String,String> tokens = Tokens.of(token1);
		
		Tokens<String,String> tokensAfterMove = tokens.moveToken(token2, state1);
		
		assertEquals(asList(token1), tokensAfterMove.findByState(state1).toList());
	}
	
	@Test
	void movesTokenThatIsOneOfTheTokens() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		
		Token<String, String> token1 = Token.create(state1, TOKEN1);
		Token<String, String> token2 = Token.create(state2, TOKEN2);
		Tokens<String,String> tokens = Tokens.of(token1);
		
		Tokens<String,String> tokensAfterMove = tokens.moveToken(token2, state1);
		
		assertEquals(asList(token1), tokensAfterMove.findByState(state1).toList());
	}
}
