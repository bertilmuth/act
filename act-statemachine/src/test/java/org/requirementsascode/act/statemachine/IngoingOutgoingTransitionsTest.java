package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

class IngoingOutgoingTransitionsTest {

	@Test
	void noOutgoingTranisitionsForStateNotPartOfStatemachine() {	
		State<Object, Object> outsideState = state("OutsideState", s -> true);
		
		Statemachine<Object, Object> statemachine = Statemachine.builder()
			.states()
			.transitions()
			.build();
		
		Transitions<Object , Object> outgoingTransitions =
			statemachine.outgoingTransitions(outsideState);
		assertTrue(outgoingTransitions.stream().toList().isEmpty());
	}

}
