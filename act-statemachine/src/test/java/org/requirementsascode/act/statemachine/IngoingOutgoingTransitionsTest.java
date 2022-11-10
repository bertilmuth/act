package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import static java.util.Arrays.asList;

import org.junit.jupiter.api.Test;

class IngoingOutgoingTransitionsTest {

	@Test
	void noOutgoingTransitionsForStateNotPartOfStatemachine() {	
		State<Object, Object> state = state("OutsideState", s -> true);
		
		Statemachine<Object, Object> statemachine = Statemachine.builder()
			.states()
			.transitions()
			.build();
		
		Transitions<Object , Object> outgoingTransitions =
			statemachine.outgoingTransitions(state);
		assertTrue(outgoingTransitions.stream().toList().isEmpty());
	}

	@Test
	void noOutgoingTransitionsForStateWithoutTransitions() {	
		State<Object, Object> state = state("StateWithoutTransitions", s -> true);
		
		Statemachine<Object, Object> statemachine = Statemachine.builder()
			.states(state)
			.transitions()
			.build();
		
		Transitions<Object , Object> outgoingTransitions =
			statemachine.outgoingTransitions(state);
		assertTrue(outgoingTransitions.stream().toList().isEmpty());
	}
	
	@Test
	void checksSingleOutgoingTransition() {	
		State<String, String> state1 = state("State1", s -> true);
		State<String, String> state2 = state("State2", s -> true);
		Transition<String, String> transition = transition(state1, state2, d -> d);
		
		Statemachine<String, String> statemachine = Statemachine.builder()
			.states(state1, state2)
			.transitions(transition)
			.build();
		
		Transitions<String , String> outgoingTransitions =
			statemachine.outgoingTransitions(state1);
		assertEquals(asList(transition), outgoingTransitions.stream().toList());
	}
}
