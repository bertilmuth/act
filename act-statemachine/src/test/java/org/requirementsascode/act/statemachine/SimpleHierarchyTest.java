package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

import static org.requirementsascode.act.statemachine.StatemachineApi.*;

class SimpleHierarchyTest {
	private static final String TOP_S1 = "top_s1";
	private static final String TOP_S2 = "top_s2";
	
	interface Trigger{ }
	class Switch implements Trigger{ }

	private Statemachine<State<?,?>, Trigger> top;

	@BeforeEach
	void test() {
		State<State<?,?>, Trigger> top_s1 = state(TOP_S1, s -> s != null && TOP_S1.equals(s.name()));
		State<State<?,?>, Trigger> top_s2 = state(TOP_S2, s -> s != null &&  TOP_S2.equals(s.name()));
		
		top =
			Statemachine.builder()
				.states(
					top_s1,
					top_s2
				)
				.transitions(
					entryFlow(top_s1, consumeWith((s,v) -> top_s1)),
					transition(top_s1, top_s2, when(Switch.class, consumeWith((s,v) -> top_s2))),
					transition(top_s2, top_s1, when(Switch.class, consumeWith((s,v) -> top_s1)))
				)
				.build();

	}

	@Test
	void switchesOnce() {
		Data<State<?, ?>, Trigger> afterInit = topInit();
		Data<State<?, ?>, Trigger> afterSwitch1 = topActOn(afterInit, new Switch());
		assertEquals(TOP_S2, afterSwitch1.state().name());
	}
	
	@Test
	void switchesTwice() {
		Data<State<?, ?>, Trigger> afterInit = topInit();
		Data<State<?, ?>, Trigger> afterSwitch1 = topActOn(afterInit, new Switch());
		Data<State<?, ?>, Trigger> afterSwitch2 = topActOn(afterSwitch1, new Switch());
		assertEquals(TOP_S1, afterSwitch2.state().name());
	}

	private Data<State<?, ?>, Trigger> topActOn(Data<State<?,?>, Trigger> before, Trigger trigger) {
		Data<State<?, ?>, Trigger> d = data(before.state(), trigger);
		return top.actOn(d);
	}

	private Data<State<?, ?>, Trigger> topInit() {
		return top.actOn(data(null, null));
	}
}
