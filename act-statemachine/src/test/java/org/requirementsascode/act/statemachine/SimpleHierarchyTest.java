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

	private Statemachine<String, Trigger> top;

	@BeforeEach
	void test() {
		State<String, Trigger> top_s1 = state(TOP_S1, TOP_S1::equals);
		State<String, Trigger> top_s2 = state(TOP_S2, TOP_S2::equals);
		
		top =
			Statemachine.builder()
				.states(
					top_s1,
					top_s2
				)
				.transitions(
					entryFlow(top_s1, consumeWith((s,v) -> TOP_S1)),
					transition(top_s1, top_s2, when(Switch.class, consumeWith((s,v) -> TOP_S2))),
					transition(top_s2, top_s1, when(Switch.class, consumeWith((s,v) -> TOP_S1)))
				)
				.build();

	}

	@Test
	void switchesOnce() {
		Data<String, Trigger> afterInit = topInit();
		Data<String, Trigger> afterSwitch1 = topActOn(afterInit, new Switch());
		assertEquals(TOP_S2, afterSwitch1.state());
	}
	
	@Test
	void switchesTwice() {
		Data<String, Trigger> afterInit = topInit();
		Data<String, Trigger> afterSwitch1 = topActOn(afterInit, new Switch());
		Data<String, Trigger> afterSwitch2 = topActOn(afterSwitch1, new Switch());
		assertEquals(TOP_S1, afterSwitch2.state());
	}

	private Data<String, Trigger> topActOn(Data<String, Trigger> before, Trigger trigger) {
		Data<String, Trigger> d = data(before.state(), trigger);
		return top.actOn(d);
	}

	private Data<String, Trigger> topInit() {
		return top.actOn(data(null, null));
	}
}