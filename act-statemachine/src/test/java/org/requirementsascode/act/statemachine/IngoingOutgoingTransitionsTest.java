package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.junit.jupiter.api.Test;

class IngoingOutgoingTransitionsTest {

	@Test
	void noOutgoingTransitionsForStateNotPartOfStatemachine() {	
		State<Object, Object> outsideState = state("OutsideState", s -> true);
		
		Statemachine<Object, Object> statemachine = Statemachine.builder()
			.states()
			.transitions()
			.build();
		
		Transitions<Object , Object> outgoingTransitions =
			statemachine.outgoingTransitions(outsideState);
		assertTrue(outgoingTransitions.stream().toList().isEmpty());
	}

	@Test
	void noOutgoingTransitionsForStateWithoutTransitions() {	
		State<Object, Object> stateWithoutTransitions = state("StateWithoutTransitions", s -> true);
		
		Statemachine<Object, Object> statemachine = Statemachine.builder()
			.states(stateWithoutTransitions)
			.transitions()
			.build();
		
		Transitions<Object , Object> outgoingTransitions =
			statemachine.outgoingTransitions(stateWithoutTransitions);
		assertTrue(outgoingTransitions.stream().toList().isEmpty());
	}
}
