package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class PlaceTest {
	@Test
	void createsPlaceForState() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String, String> place = Place.forState(state1);
		assertEquals(state1, place.state());
		assertTrue(place.tokens().isEmpty());
	}

	@Test
	void putsTokenOnPlace() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String, String> place = Place.forState(state1);
		Place<String, String> placeWithToken1 = place.putToken("Token1");
		assertEquals(asList("Token1"), placeWithToken1.tokens());
	}

	@Test
	void putsDifferentTokenOnPlace() {
		State<Number, Number> state1 = state("State1", s -> true);
		Place<Number, Number> place = Place.forState(state1);
		Place<Number, Number> placeWithToken1 = place.putToken(5);
		assertEquals(asList(5), placeWithToken1.tokens());
	}

	@Test
	void putsTwoTokensOnPlace() {
		State<Token, Token> state1 = state("State1", s -> true);
		TokenType1 token1 = new TokenType1();
		TokenType2 token2 = new TokenType2();

		Place<Token, Token> placeWithTwoTokens = Place.forState(state1).withTokens(token1, token2);
		assertEquals(asList(token1, token2), placeWithTwoTokens.tokens());
	}

	@Test
	void putsTokenOnPlaceThatHasToken() {
		State<Token, Token> state1 = state("State1", s -> true);
		TokenType1 token1 = new TokenType1();
		TokenType2 token2 = new TokenType2();

		Place<Token, Token> placeWithOneToken = Place.forState(state1).withTokens(token1);
		Place<Token, Token> placeWithTwoTokens = placeWithOneToken.putToken(token2);

		assertEquals(asList(token1, token2), placeWithTwoTokens.tokens());
		assertEquals(asList(token1), placeWithOneToken.tokens());
	}

	private interface Token {
	};

	private class TokenType1 implements Token {
	};

	private class TokenType2 implements Token {
	};
}
