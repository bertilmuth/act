package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.entryFlow;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

class FlatStatemachineTest {
	private static final String TOP_S1 = "top_s1";
	private static final String TOP_S2 = "top_s2";
	
	interface Trigger{ }
	class Switch implements Trigger{ }
	class Init implements Trigger{ }

	private Statemachine<State<?,?>, Trigger> top;

	@BeforeEach
	void test() {
		BasicState<Trigger> top_s1 = basicState(TOP_S1);
		BasicState<Trigger> top_s2 = basicState(TOP_S2);
		
		top =
			Statemachine.builder()
				.states(
					top_s1,
					top_s2
				)
				.transitions(
					initial(top_s1),
					transition(top_s1, top_s2, Switch.class),
					transition(top_s2, top_s1, Switch.class)
				)
				.build();
	}

	@Test
	void switchesOnce() {
		Data<State<?, ?>, Trigger> afterInit = topInit();
		Data<State<?, ?>, Trigger> afterSwitch1 = top.act(afterInit, new Switch());
		assertEquals(TOP_S2, afterSwitch1.state().name());
	}
	
	@Test
	void switchesTwice() {
		Data<State<?, ?>, Trigger> afterInit = topInit();
		Data<State<?, ?>, Trigger> afterSwitch1 = top.act(afterInit, new Switch());
		Data<State<?, ?>, Trigger> afterSwitch2 = top.act(afterSwitch1, new Switch());
		assertEquals(TOP_S1, afterSwitch2.state().name());
	}

	private Data<State<?, ?>, Trigger> topInit() {
		return top.actOn(data(null, new Init()));
	}
	
	private <T> TriggeredTransition<State<?, ?>, T> transition(BasicState<T> from, BasicState<T> to, Class<? extends T> triggerClass) {
		return StatemachineApi.transition(from, to, when(triggerClass, consumeWith((s, v) -> to)));
	}
	
	private <T> EntryFlow<State<?, ?>, T> initial(BasicState<T> state) {
		return entryFlow(state, consumeWith((s,v) -> state));
	}
	
	private <T> BasicState<T> basicState(String stateName) {
		return new BasicState<>(stateName);
	}
	
	class BasicState<T> extends State<State<?,?>, T>{
		public BasicState(String stateName) {
			super(stateName, s -> s != null && stateName.equals(s.name()));
		}
	}
}
