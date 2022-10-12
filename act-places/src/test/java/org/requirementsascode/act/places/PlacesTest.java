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
	void setsTokenOnPlace() {
		State<String, String> state1 = state("State1", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.setTokens(state1, asList(TOKEN1));

		assertEquals(TOKEN1, newPlaces.nextToken(state1).get());
	}
	
	@Test
	void setsTwoTokensOnPlace() {
		State<String, String> state1 = state("State1", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.setTokens(state1, asList("TokenThatWillBeOverwritten"))
			.setTokens(state1, asList(TOKEN1, TOKEN2));

		assertEquals(TOKEN1, newPlaces.nextToken(state1).get());
		assertEquals(TOKEN2, newPlaces.nextToken(state1).get());
	}

	@Test
	void ignoresSettingTokensForStateThatsNotPartOfStatemachine() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.setTokens(state2, asList(TOKEN2));

		assertFalse(newPlaces.nextToken(state2).isPresent());
	}
	
	@Test
	void addsTokenToPlace() {
		State<String, String> state1 = state("State1", s -> true);
		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();
		
		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.setTokens(state1, asList(TOKEN1))
			.addToken(state1, TOKEN2);
		
		assertEquals(TOKEN1, newPlaces.nextToken(state1).get());
		assertEquals(TOKEN2, newPlaces.nextToken(state1).get());
	}
	
	@Test
	void ignoresAddingTokenForStateThatsNotPartOfStatemachine() {
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);

		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();

		Places<String, String> newPlaces = Places.forStatemachine(statemachine)
			.addToken(state2, TOKEN2);

		assertFalse(newPlaces.nextToken(state2).isPresent());
	}
}
