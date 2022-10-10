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
}
