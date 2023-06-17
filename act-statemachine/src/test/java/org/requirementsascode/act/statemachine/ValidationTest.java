package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.testdata.HierarchicalCart;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

class ValidationTest {
	@Test
	void allTransitionFromStatesMustBeInStateList() {
		State<HierarchicalCart,Trigger> firstState = state("Empty Cart", cs -> false);
		State<HierarchicalCart, Trigger> secondState = state("Non-empty Cart", cs -> true);

		assertThrows(IllegalArgumentException.class, () -> 
			Statemachine.builder() 
				.states(secondState) // The first state is missing from the state list, but it's used in a transition -> exception expected
				.transitions(
					triggeredTransition(firstState, secondState, when(AddItem.class, consumeWith((s,t) -> s)))
				)
			.build()
		);
	}
	
	@Test
	void allTransitionToStatesMustBeInStateList() {
		State<HierarchicalCart,Trigger> firstState = state("Empty Cart", cs -> false);
		State<HierarchicalCart, Trigger> secondState = state("Non-empty Cart", cs -> true);

		assertThrows(IllegalArgumentException.class, () -> 
			Statemachine.builder()
				.states(firstState) // The second state is missing from the state list, but it's used in a transition -> exception expected
				.transitions(
					triggeredTransition(firstState, secondState, when(AddItem.class, consumeWith((s,t) -> s)))
				)
			.build()
		);
	}
}

