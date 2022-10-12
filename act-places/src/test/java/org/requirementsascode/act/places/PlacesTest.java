package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class PlacesTest {
	@Test
	void doesntFindNonExistingPlace() {
		State<Object,Object> state1 = state("State1", s -> true);
		Statemachine<Object, Object> statemachine = Statemachine.builder()
				.states().transitions().build();
		
		Places<Object,Object> places = Places.forStatemachine(statemachine);
		Optional<Place<Object,Object>> place = places.findByState(state1);
		assertTrue(place.isEmpty());
	}
	
	@Test
	void findsPlaceByState() {
		State<String,String> state1 = state("State1", s -> true);
		State<String,String> state2 = state("State2", s -> true);
		Statemachine<String, String> statemachine = Statemachine.builder()
				.states(state1, state2)
				.transitions()
				.build();
		
		Places<String, String> places = Places.forStatemachine(statemachine);
		Optional<Place<String, String>> place1 = places.findByState(state1);
		assertEquals(state1, place1.get().state());
	}
	
	@Test
	void findsDifferentPlaceByState() {
		State<String,String> state1 = state("State1", s -> true);
		State<String,String> state2 = state("State2", s -> true);
		Statemachine<String, String> statemachine = Statemachine.builder()
				.states(state1, state2)
				.transitions()
				.build();
		
		Places<String, String> places = Places.forStatemachine(statemachine);
		Optional<Place<String, String>> place2 = places.findByState(state2);
		assertEquals(state2, place2.get().state());
	}
	
	@Test
	void updatesPlace() {
		State<String,String> state1 = state("State1", s -> true);
		State<String,String> state2 = state("State2", s -> true);
		
		Statemachine<String, String> statemachine = Statemachine.builder()
				.states(state1, state2)
				.transitions()
				.build();
		
		Places<String, String> places = Places.forStatemachine(statemachine);
		Places<String, String> newPlaces = places.updatePlace(state1, asList("Token1"));
		
		assertEquals("Token1", newPlaces.findByState(state1).flatMap(Place::nextToken).get());
	}
}
