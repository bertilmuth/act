package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

class InvariantTest {
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";
	private State<String, String> statemachineState1;
	private State<String, String> statemachineState2;
	
	@BeforeEach
	void setup() {
		statemachineState1 = state(STATE1, s -> s.equals(STATE1));
		statemachineState2 = state(STATE2, s -> s.equals(STATE2));
	}
	
	@Test
	void invariantOfToStateIsFulfilledAfterTransition() {
		Statemachine<String,String> statemachine = 
			Statemachine.builder()
				.states(statemachineState1, statemachineState2)
				.transitions(
					transition(statemachineState1, statemachineState2, consumeWith((s,v) -> STATE2))
				).build();
		
		Data<String, String> anyEventInState1 = data(STATE1, "AnyEvent");
		assertEquals(STATE2, statemachine.actOn(anyEventInState1).state());
	}
	
	@Test
	void exceptionIfInvariantOfToStateIsNotFulfilled() {
		Statemachine<String,String> statemachine = 
			Statemachine.builder()
				.states(statemachineState1, statemachineState2)
				.transitions(
					transition(statemachineState1, statemachineState2, consumeWith((s,v) -> STATE1))
				).build();
		
		Data<String, String> anyEventInState1 = data(STATE1, "AnyEvent");
		assertThrows(IllegalStateException.class, () -> statemachine.actOn(anyEventInState1).state());
	}
	
	@Test
	void exceptionIfStateBehaviorChangesState() {
		State<String, String> state1 = 
			state(STATE1, s -> s.equals(STATE1), d -> data(STATE2, d.value().get()));

		Statemachine<String,String> statemachine = 
			Statemachine.builder()
				.states(state1)
				.transitions()
				.build();
		
		Data<String, String> eventInState1 = data(STATE1, "AnyEvent");
		assertThrows(IllegalStateException.class, () -> statemachine.actOn(eventInState1));
	}
	
	@Test
	void exceptionIfStateBehaviorChangesStateInToState() {
		State<String, String> state2 = 
			state(STATE2, s -> s.equals(STATE2), d -> data(STATE1, d.value().get()));

		Statemachine<String,String> statemachine = 
			Statemachine.builder()
				.states(statemachineState1, state2)
				.transitions(
						transition(statemachineState1, state2, consumeWith((s,v) -> STATE2))
				)
				.build();
		
		Data<String, String> eventInState1 = data(STATE1, "AnyEvent");
		assertThrows(IllegalStateException.class, () -> statemachine.actOn(eventInState1));
	}
}
