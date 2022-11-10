package org.requirementsascode.act.statemachine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

class ToStateBehaviorTest {

	private static final String S1 = "s1";
	private static final String S2 = "s2";

	private boolean s2Entered = false;
	private Statemachine<String, Trigger> statemachine;
	
	@BeforeEach
	void setup() {
		State<String, Trigger> s1 = State.state(S1, s -> S1.equals(s));
		State<String, Trigger> s2 = State.state(S2, s -> S2.equals(s), consumeWith(this::enterS2));
		
		statemachine = Statemachine.builder()
				.states(s1, s2)
				.transitions(
						transition(s1,s2, 
							when(EnterS2.class, consumeWith((s,t) -> S2))
						))
				.build();
	}

	@Test
	void entersS2() {
		Data<String, Trigger> result = statemachine.actOn(data(S1, new EnterS2()));
		assertThat(result.state()).isEqualTo(S2);
		assertThat(s2Entered).isEqualTo(true);
	}
	
	@Test
	void doesntEnterS2() {
		Data<String, Trigger> result = statemachine.actOn(data(S1, new DontEnterS2()));
		assertThat(result.state()).isEqualTo(S1);
		assertThat(s2Entered).isEqualTo(false);
	}
	
	interface Trigger {}
	class EnterS2 implements Trigger{};
	class DontEnterS2 implements Trigger{};
	
	String enterS2(String state, Trigger event) {
		s2Entered = true;
		return S2;
	}
}
