package org.requirementsascode.act.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.requirementsascode.act.core.Data.data;

import static org.requirementsascode.act.statemachine.StatemachineApi.*;

class ToStateBehaviorTest {

	private static final String S1 = "s1";
	private static final String S2 = "s2";

	private boolean s2Entered = false;
	private Statemachine<String, String> statemachine;
	
	@BeforeEach
	void setup() {
		State<String, String> s1 = State.state(S1, s -> S1.equals(s));
		State<String, String> s2 = State.state(S2, s -> S2.equals(s));
		
		statemachine = Statemachine.builder()
				.states(s1, s2)
				.transitions(
						transition(s1,s2, consumeWith((state, str) -> {
							s2Entered = true;
							return S2;
						}))
					)
				.build();
	}

	@Test
	void entersS2() {
		Data<String, String> result = statemachine.actOn(data(S1, "event"));
		assertThat(result.state()).isEqualTo(S2);
		assertThat(s2Entered).isEqualTo(true);
	}
}