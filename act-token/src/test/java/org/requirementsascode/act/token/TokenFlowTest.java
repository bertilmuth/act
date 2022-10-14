package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class TokenFlowTest {
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";

	@Test
	void test() {
		State<Tokens<Trigger>, Trigger> state1 = state(STATE1, tokens -> tokens.inState(STATE1).count() != 0);
		State<Tokens<Trigger>, Trigger> state2 = state(STATE2, tokens -> tokens.inState(STATE2).count() != 0);
		
		Statemachine<Tokens<Trigger>, Trigger> statemachine =
			Statemachine.builder()
				.states(state1, state2)
				.transitions(
					transition(state1, state2, consumeWith((s,v) -> s))
				)
				.build();
	}

	private static interface Trigger{};
}
