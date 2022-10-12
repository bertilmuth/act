package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class PlacesTest {
	private static final String TOKEN2 = "Token2";
	private static final String TOKEN1 = "Token1";

	@Test
	void updatesPlaceWithOneToken() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder().states(state1, state2).transitions().build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.placeTokens(state1, asList(TOKEN1));

		assertEquals(TOKEN1, newPlaces.nextToken(state1));
	}
	
	@Test
	void updatesPlaceWithTwoTokens() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder().states(state1, state2).transitions().build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.placeTokens(state1, asList(TOKEN1, TOKEN2));

		assertEquals(TOKEN1, newPlaces.nextToken(state1));
		assertEquals(TOKEN2, newPlaces.nextToken(state1));
	}

	@Test
	void ignoresUpdatedOfPlaceForStateThatsNotPartOfStatemachine() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder().states(state1).transitions().build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.placeTokens(state2, asList(TOKEN2));

		assertFalse(newPlaces.findByState(state2).isPresent());
	}
}
