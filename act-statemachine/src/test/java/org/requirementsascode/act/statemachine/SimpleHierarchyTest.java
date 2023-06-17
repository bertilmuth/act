package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.entryFlow;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

class SimpleHierarchyTest {
	private static final String TOP_S1 = "top_s1";
	private static final String TOP_S2 = "top_s2";
	
	interface Trigger{ }
	class Switch implements Trigger{ }

	private Statemachine<State<?,?>, Trigger> top;

	@BeforeEach
	void test() {
		State<State<?,?>, Trigger> top_s1 = state(TOP_S1);
		State<State<?,?>, Trigger> top_s2 = state(TOP_S2);
		
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
	
	private State<State<?, ?>, Trigger> state(String stateName) {
		return StatemachineApi.state(stateName, s -> s != null && stateName.equals(s.name()));
	}
}
