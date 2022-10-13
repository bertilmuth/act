package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.token.Token.token;

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
			token(TOKEN1, state1),
			token(TOKEN2, state2)
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
			token(TOKEN1, state1)
		);
		
		List<Token<String, String>> tokensInState2 = tokens.inState(state2).toList();
		assertTrue(tokensInState2.isEmpty());
	}
	
	@Test
	void findsTokenForStateWhereOneTokenHasBeenPlaced() {
		State<String, String> state1 = state("State1", s -> true);
		
		Tokens<String,String> tokens = Tokens.of(
			token(TOKEN1, state1)
		);
		
		List<Token<String, String>> tokensInState1 = tokens.inState(state1).toList();
		assertEquals(TOKEN1, tokensInState1.get(0).value());
	}
	
	@Test
	void findsTokensForStateWhereTwoTokensHaveBeenPlaced() {
		State<String, String> state1 = state("State1", s -> true);
		
		Tokens<String,String> tokens = Tokens.of(
			token(TOKEN1, state1),
			token(TOKEN2, state1)
		);
		
		List<Token<String, String>> tokensInState1 = tokens.inState(state1).toList();
		assertEquals(TOKEN1, tokensInState1.get(0).value());
		assertEquals(TOKEN2, tokensInState1.get(1).value());
	}
	
	@Test
	void findsTokensForStateWhereTwoTokensHaveBeenPlaced_whenTheresAnotherState() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		
		Tokens<String,String> tokens = Tokens.of(
			token(TOKEN1, state1),
			token(TOKEN2, state2),
			token(TOKEN1, state2)
		);
		
		List<Token<String, String>> tokensInState2 = tokens.inState(state2).toList();
		assertEquals(TOKEN2, tokensInState2.get(0).value());
		assertEquals(TOKEN1, tokensInState2.get(1).value());
	}
	
	@Test
	void doesntMoveTokenThatIsntOneOfTheTokens() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		
		Token<String, String> token1 = token(TOKEN1, state1);
		Token<String, String> token2 = token(TOKEN2, state2);
		Tokens<String,String> tokensBefore = Tokens.of(token1);
		
		Tokens<String,String> tokensAfter = tokensBefore.moveToken(token2, state1);
		
		assertEquals(asList(token1), tokensAfter.inState(state1).toList());
	}
	
	@Test
	void movesTokenThatIsOneOfTheTokens() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		
		Token<String, String> token1 = token(TOKEN1, state1);
		Token<String, String> token2 = token(TOKEN2, state2);
		Tokens<String,String> tokensBefore = Tokens.of(token1, token2);
		
		Tokens<String,String> tokensAfter = tokensBefore.moveToken(token2, state1);
		
		assertEquals(asList(token1, token2), tokensAfter.inState(state1).toList());
	}
}
