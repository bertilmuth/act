package org.requirementsascode.act.token;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.Tokens.tokens;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class TokensTest {
	private static final String TOKEN1 = "Token1";
	private static final String TOKEN2 = "Token2";
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";

	@Test
	void createsEmptyTokens() {
		Tokens<String> tokens = tokens();
		
		List<?> tokenList = tokens.stream().toList();
		assertTrue(tokenList.isEmpty());
	}

	@Test
	void createsTokensWithTwoElements() {
		State<String, String> state1 = state(STATE1, s -> true);
		State<String, String> state2 = state(STATE2, s -> true);

		Tokens<String> tokens = tokens(
			token(TOKEN1, state1),
			token(TOKEN2, state2)
		);
		
		List<Token<String>> tokenList = tokens.stream().toList();
		assertEquals(TOKEN1, tokenList.get(0).value());
		assertEquals(TOKEN2, tokenList.get(1).value());
	}
	
	@Test
	void findsNoTokensForStateWhereNoTokensHaveBeenPlaced() {
		State<String, String> state1 = state(STATE1, s -> true);

		Tokens<String> tokens = tokens(
			token(TOKEN1, state1)
		);
		
		List<Token<String>> tokensInState2 = tokens.inState(STATE2).toList();
		assertTrue(tokensInState2.isEmpty());
	}
	
	@Test
	void findsTokenForStateWhereOneTokenHasBeenPlaced() {
		State<String, String> state1 = state(STATE1, s -> true);
		
		Tokens<String> tokens = tokens(
			token(TOKEN1, state1)
		);
		
		List<Token<String>> tokensInState1 = tokens.inState(STATE1).toList();
		assertEquals(TOKEN1, tokensInState1.get(0).value());
	}
	
	@Test
	void findsTokensForStateWhereTwoTokensHaveBeenPlaced() {
		State<String, String> state1 = state(STATE1, s -> true);
		
		Tokens<String> tokens = tokens(
			token(TOKEN1, state1),
			token(TOKEN2, state1)
		);
		
		List<Token<String>> tokensInState1 = tokens.inState(STATE1).toList();
		assertEquals(TOKEN1, tokensInState1.get(0).value());
		assertEquals(TOKEN2, tokensInState1.get(1).value());
	}
	
	@Test
	void findsTokensForStateWhereTwoTokensHaveBeenPlaced_whenTheresAnotherState() {
		State<String, String> state1 = state(STATE1, s -> true);
		State<String, String> state2 = state(STATE2, s -> true);
		
		Tokens<String> tokens = tokens(
			token(TOKEN1, state1),
			token(TOKEN2, state2),
			token(TOKEN1, state2)
		);
		
		List<Token<String>> tokensInState2 = tokens.inState(STATE2).toList();
		assertEquals(TOKEN2, tokensInState2.get(0).value());
		assertEquals(TOKEN1, tokensInState2.get(1).value());
	}
	
	@Test
	void doesntMoveTokenThatIsntOneOfTheTokens() {
		State<String, String> state1 = state(STATE1, s -> true);
		State<String, String> state2 = state(STATE2, s -> true);
		
		Token<String> token1 = token(TOKEN1, state1);
		Token<String> token2 = token(TOKEN2, state2);
		Tokens<String> tokensBefore = tokens(token1);
		
		Tokens<String> tokensAfter = tokensBefore.moveToken(token2, state1);
		
		assertEquals(asList(token1), tokensAfter.inState(STATE1).toList());
	}
	
	@Test
	void movesTokenThatIsOneOfTheTokens() {
		State<String, String> state1 = state(STATE1, s -> true);
		State<String, String> state2 = state(STATE2, s -> true);
		
		Token<String> token1 = token(TOKEN1, state1);
		Token<String> token2 = token(TOKEN2, state2);
		Tokens<String> tokensBefore = tokens(token1, token2);
		
		Tokens<String> tokensAfter = tokensBefore.moveToken(token2, state1);
		
		assertEquals(asList(token1, token2), tokensAfter.inState(STATE1).toList());
	}
}
