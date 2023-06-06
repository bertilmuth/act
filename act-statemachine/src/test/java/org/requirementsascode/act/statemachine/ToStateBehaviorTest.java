package org.requirementsascode.act.statemachine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

class ToStateBehaviorTest {

	private static final String S1 = "s1";
	private static final String S2 = "s2";

	private boolean s2Entered = false;
	private Statemachine<String, Trigger> statemachine;
	
	@BeforeEach
	void setup() {
		State<String, Trigger> s1 = state(S1, s -> S1.equals(s));
		State<String, Trigger> s2 = state(S2, s -> S2.equals(s), enterS2());
		
		statemachine = Statemachine.builder()
				.states(s1, s2)
				.transitions(
					transition(s1,s2, 
						when(EnterNextState.class, d -> data(S2, d.value()))),
					transition(s2,s1, 
						when(EnterNextState.class, d -> data(S1, d.value())))
					)
				.build();
	}

	@Test
	void entersS2() {
		Data<String, Trigger> data = data(S1, new EnterNextState());
		assertThat(handle(data)).isEqualTo(S2);
		assertTrue(s2Entered);
	}

	
	@Test
	void doesntEnterS2() {
		Data<String, Trigger> data = data(S1, new DontEnterNextState());
		assertThat(handle(data)).isEqualTo(S1);
		assertFalse(s2Entered);
	}
	
	private String handle(Data<String, Trigger> data) {
		return statemachine.actOn(data).state();
	}
	
	interface Trigger {}
	class EnterNextState implements Trigger{};
	class DontEnterNextState implements Trigger{};
	
	Behavior<String,Trigger,Trigger> enterS2() {
		return d -> {
			s2Entered = true;
			return data(S2, d.value());
		};
	}
}
