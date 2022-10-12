package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class PlacesTest {

	@Test
	void createsPlacesForStatemachineWithoutStates() {
		Statemachine<Object, Object> statemachine = Statemachine.builder()
			.states().transitions().build();
		
		Places<Object,Object> places = Places.forStatemachine(statemachine);
		List<Place<Object,Object>> placesList = places.asList();
		assertTrue(placesList.isEmpty());
	}

	@Test
	void createsPlacesForStatemachineWithOneState() {
		State<String,String> state1 = state("State1", s -> true);
		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();
		
		Places<String, String> places = Places.forStatemachine(statemachine);
		List<Place<String, String>> placesList = places.asList();
		assertEquals(1, placesList.size());
		assertEquals(state1, placesList.get(0).state());
	}
	
	@Test
	void createsPlacesForStatemachineWithDifferentState() {
		State<Integer,Integer> state1 = state("DifferentState", s -> true);
		Statemachine<Integer, Integer> statemachine = Statemachine.builder()
			.states(state1)
			.transitions()
			.build();
		
		Places<Integer, Integer> places = Places.forStatemachine(statemachine);
		List<Place<Integer, Integer>> placesList = places.asList();
		assertEquals(1, placesList.size());
		assertEquals(state1, placesList.get(0).state());
	}
	
	@Test
	void createsPlacesForStatemachineWithTwoStates() {
		State<Integer,Integer> state1 = state("State1", s -> true);
		State<Integer,Integer> state2 = state("State2", s -> true);
		Statemachine<Integer, Integer> statemachine = Statemachine.builder()
			.states(state1,state2)
			.transitions()
			.build();
		
		Places<Integer, Integer> places = Places.forStatemachine(statemachine);
		List<Place<Integer, Integer>> placesList = places.asList();
		assertEquals(2, placesList.size());
		assertEquals(state1, placesList.get(0).state());
		assertEquals(state2, placesList.get(1).state());
	}
	
	@Test
	void doesntFindNonExistingState() {
		State<Integer,Integer> state1 = state("State1", s -> true);
		Statemachine<Object, Object> statemachine = Statemachine.builder()
				.states().transitions().build();
		
		Places<Object,Object> places = Places.forStatemachine(statemachine);
		Optional<Place<Object,Object>> state = places.findByState(state1);
		assertTrue(state.isEmpty());
	}
}
