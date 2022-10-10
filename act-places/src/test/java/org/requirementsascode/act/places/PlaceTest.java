package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		place.put("Token1");
		assertEquals("Token1", place.tokens().get(0));
	}
	
	@Test
	void putsDifferentTokenOnPlace() {
		State<Number, Number> state1 = state("State1", s -> true);
		Place<Number,Number> place = Place.forState(state1);
		place.put(5);
		assertEquals(5, place.tokens().get(0));
	}
	

	@Test
	void putsTwoTokensOnPlace() {
		State<Token, Token> state1 = state("State1", s -> true);
		Place<Token,Token> place = Place.forState(state1);
		TokenType1 token1 = new TokenType1();
		TokenType2 token2 = new TokenType2();

		place.put(token1);
		place.put(token2);
		assertEquals(asList(token1, token2), place.tokens());
	}
	
	private interface Token{};
	private class TokenType1 implements Token{};
	private class TokenType2 implements Token{};
}
