package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transit.transit;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.CartState;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

class ValidationTest {
	@Test
	void allTransitionFromStatesMustBeInStateList() {
		State<CartState,Trigger> firstState = state("Empty Cart", cs -> false);
		State<CartState, Trigger> secondState = state("Non-empty Cart", cs -> true);

		assertThrows(IllegalArgumentException.class, () -> 
			Statemachine.builder() 
				.states(secondState) // The first state is missing from the state list, but it's used in a transition -> exception expected
				.transitions(
					transition(firstState, secondState, when(AddItem.class, transit((s,t) -> s)))
				)
			.build()
		);
	}
	
	@Test
	void allTransitionToStatesMustBeInStateList() {
		State<CartState,Trigger> firstState = state("Empty Cart", cs -> false);
		State<CartState, Trigger> secondState = state("Non-empty Cart", cs -> true);

		assertThrows(IllegalArgumentException.class, () -> 
			Statemachine.builder()
				.states(firstState) // The second state is missing from the state list, but it's used in a transition -> exception expected
				.transitions(
					transition(firstState, secondState, when(AddItem.class, transit((s,t) -> s)))
				)
			.build()
		);
	}
}

