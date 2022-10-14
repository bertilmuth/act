package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.Tokens.tokens;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class TokenFlowTest {
	private static final String VALUE1 = "Value1";
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";

	@Test
	void test() {
		State<Tokens<Trigger>, Trigger> state1 = state(STATE1, tokens -> tokens.inState(STATE1).count() != 0, 
			d -> publishToken(STATE1, d));
		
		State<Tokens<Trigger>, Trigger> state2 = state(STATE2, tokens -> tokens.inState(STATE2).count() != 0,
			d -> publishToken(STATE2, d));
		
		Statemachine<Tokens<Trigger>, Trigger> statemachine =
			Statemachine.builder()
				.states(state1, state2)
				.transitions(
					transition(state1, state2, 
						consumeWith((tokens,value) -> {
							Tokens<Trigger> newTokens = 
								tokens.moveToken(token(value, state1), state2);
							return newTokens;
						}))
				)
				.build();
		
		Tokens<Trigger> tokens = tokens(
				token(new Value(VALUE1), state1)
		);
		
		Data<Tokens<Trigger>, Trigger> dataAfter = statemachine.actOn(data(tokens, new Tick()));
		assertFalse(dataAfter.state().inState(STATE1).findAny().isPresent());
		assertEquals(token(new Value(VALUE1), state2), dataAfter.state().inState(STATE2).findFirst().get());
	}

	private <V> Data<Tokens<V>, V> publishToken(String stateName, Data<Tokens<V>, V> data) {
			V firstTokenValue = data.state().inState(stateName)
				.findFirst()
				.map(t -> t.value())
				.orElse(null);
			return data(data.state(), firstTokenValue);
	}
	
	private static interface Trigger {};
	private static class Tick implements Trigger{};

	private static class Value implements Trigger{
		public final String string;
		public Value(String string) {
			this.string = string;
		}
		@Override
		public int hashCode() {
			return Objects.hash(string);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Value other = (Value) obj;
			return Objects.equals(string, other.string);
		}
	};
}
