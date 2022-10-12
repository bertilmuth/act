package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class PlaceTest {
	private static final String TOKEN1 = "Token1";

	@Test
	void createsPlaceForState() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String, String> place = Place.forState(state1);
		assertEquals(state1, place.state());
		assertEquals(0, place.size());
	}
	
	@Test
	void ignoresNextForEmptyPlace() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String, String> place = Place.forState(state1);
		Place<String, String> newPlace = place.next();
		assertEquals(state1, newPlace.state());
		assertEquals(0, newPlace.size());
	}

	@Test
	void putsTokenOnPlace() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String, String> place = Place.forState(state1);
		Place<String, String> placeWithToken1 = place.addToken(TOKEN1);

		assertEquals(0, place.size());
		assertEquals(1, placeWithToken1.size());
		assertEquals(TOKEN1, placeWithToken1.token().get());
		
		assertFalse(placeWithToken1.next().token().isPresent());
	}

	@Test
	void putsDifferentTokenOnPlace() {
		State<Number, Number> state1 = state("State1", s -> true);
		Place<Number, Number> placeWithToken1 = Place.forState(state1).addToken(5);

		assertEquals(1, placeWithToken1.size());
		assertEquals(5, placeWithToken1.token().get());
		assertFalse(placeWithToken1.next().token().isPresent());
	}

	@Test
	void putsTwoTokensOnPlace() {
		State<Token, Token> state1 = state("State1", s -> true);
		TokenType1 token1 = new TokenType1();
		TokenType2 token2 = new TokenType2();
		Place<Token, Token> placeWithTwoTokens = Place.forState(state1).withTokens(asList(token1, token2));

		assertEquals(2, placeWithTwoTokens.size());
		assertEquals(token1, placeWithTwoTokens.token().get());
		assertEquals(token2, placeWithTwoTokens.next().token().get());
		assertFalse(placeWithTwoTokens.next().next().token().isPresent());
	}

	@Test
	void putsTokenOnPlaceThatHasToken() {
		State<Token, Token> state1 = state("State1", s -> true);
		TokenType1 token1 = new TokenType1();
		TokenType2 token2 = new TokenType2();

		Place<Token, Token> placeWithOneToken = Place.forState(state1).withTokens(asList(token1));
		Place<Token, Token> placeWithTwoTokens = placeWithOneToken.addToken(token2);

		assertEquals(2, placeWithTwoTokens.size());
		assertEquals(token1, placeWithTwoTokens.token().get());
		assertEquals(token2, placeWithTwoTokens.next().token().get());
		assertFalse(placeWithTwoTokens.next().next().token().isPresent());
	}

	private interface Token {
	};

	private class TokenType1 implements Token {
	};

	private class TokenType2 implements Token {
	};
}
