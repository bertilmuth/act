package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
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

}
