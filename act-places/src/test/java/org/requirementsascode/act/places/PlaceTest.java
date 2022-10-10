package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

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
}
