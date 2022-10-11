package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;

class PlaceTest {
	@Test
	void createsPlaceForState() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String,String> place = Place.forState(state1);
		assertEquals(state1, place.state());
	}
	
	@Test
	void putsTokenOnPlace() {
		State<String, String> state1 = state("State1", s -> true);
		Place<String,String> place = Place.forState(state1);
		Place<String,String> placeWithToken1 = place.putToken("Token1");
		assertEquals("Token1", placeWithToken1.tokens().get(0));
	}
	
	@Test
	void putsDifferentTokenOnPlace() {
		State<Number, Number> state1 = state("State1", s -> true);
		Place<Number,Number> place = Place.forState(state1);
		Place<Number,Number> placeWithToken1 = place.putToken(5);
		assertEquals(5, placeWithToken1.tokens().get(0));
	}
	

	@Test
	void putsTokenOnPlaceThatHasToken() {
		State<Token, Token> state1 = state("State1", s -> true);
		TokenType1 token1 = new TokenType1();
		TokenType2 token2 = new TokenType2();
		
		Place<Token,Token> placeWithOneToken = Place.forState(state1).withTokens(token1);
		Place<Token,Token> placeWithTwoTokens = placeWithOneToken.putToken(token2);
		
		assertEquals(asList(token1, token2), placeWithTwoTokens.tokens());
		assertEquals(1, placeWithOneToken.tokens().size());
	}
	
	private interface Token{};
	private class TokenType1 implements Token{};
	private class TokenType2 implements Token{};
}
