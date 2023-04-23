package org.requirementsascode.act.statemachine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

class ToStateBehaviorTest {

	private static final String S1 = "s1";
	private static final String S2 = "s2";

	private boolean s1Entered = false;
	private boolean s2Entered = false;
	private Statemachine<String, Trigger> statemachine;
	
	@BeforeEach
	void setup() {
		State<String, Trigger> s1 = state(S1, s -> S1.equals(s), consumeWith(this::enterS1));
		State<String, Trigger> s2 = state(S2, s -> S2.equals(s), consumeWith(this::enterS2));
		
		statemachine = Statemachine.builder()
				.states(s1, s2)
				.transitions(
						transition(s1,s2, 
							when(EnterNextState.class, consumeWith((s,t) -> S2))),
						transition(s2,s1, 
							when(EnterNextState.class, consumeWith((s,t) -> S2))
						))
				.build();
	}

	@Test
	void entersS2() {
		Data<String, Trigger> result = statemachine.actOn(data(S1, new EnterNextState()));
		assertThat(result.state()).isEqualTo(S2);
		assertTrue(s2Entered);
		assertFalse(s1Entered);
	}
	
	@Test
	void doesntEnterS2() {
		Data<String, Trigger> result = statemachine.actOn(data(S1, new DontEnterNextState()));
		assertThat(result.state()).isEqualTo(S1);
		assertTrue(s1Entered);
		assertFalse(s2Entered);
	}
	
	interface Trigger {}
	class EnterNextState implements Trigger{};
	class DontEnterNextState implements Trigger{};
	
	String enterS1(String state, Trigger event) {
		s1Entered = true;
		s2Entered = false;
		return S1;
	}
	
	String enterS2(String state, Trigger event) {
		s1Entered = false;
		s2Entered = true;
		return S2;
	}
}
