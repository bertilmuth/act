package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.Tokens.tokens;
import static org.requirementsascode.act.token.TransmitTokens.transmitTokens;

import java.util.Objects;
import java.util.Optional;

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
		State<Tokens<Trigger>, Trigger> action1 = action(STATE1);
		
		State<Tokens<Trigger>, Trigger> action2 = action(STATE2);
		
		Statemachine<Tokens<Trigger>, Trigger> statemachine =
			Statemachine.builder()
				.states(action1, action2)
				.transitions(
						transmitTokens(action1, action2)
				)
				.build();
		
		Value value1 = new Value(VALUE1);
		
		Tokens<Trigger> tokens = tokens(
				token(value1, action1)
		);
		
		Data<Tokens<Trigger>, Trigger> dataAfter = statemachine.actOn(data(tokens));
		Tokens<Trigger> tokensAfter = dataAfter.state();
		
		assertFalse(isAnyTokenInState(tokensAfter, STATE1));
		assertEquals(token(value1, action2), firstTokenInState(tokensAfter, STATE2).get());
	}

	private <V> State<Tokens<V>, V> action(String stateName) {
		return state(stateName, tokens -> isAnyTokenInState(tokens, stateName), 
			d -> publishToken(stateName, d));
	}

	private <V> boolean isAnyTokenInState(Tokens<V> tokens, String stateName) {
		return tokens.inState(stateName).count() != 0;
	}

	private <V> Data<Tokens<V>, V> publishToken(String stateName, Data<Tokens<V>, V> data) {
			V firstTokenValue = firstTokenInState(data.state(), stateName)
				.map(t -> t.value())
				.orElse(null);
			return data(data.state(), firstTokenValue);
	}

	private <V> Optional<Token<V>> firstTokenInState(Tokens<V> tokens, String stateName) {
		return tokens.inState(stateName).findFirst();
	}
	
	private static interface Trigger {};
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
