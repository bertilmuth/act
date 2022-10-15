package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.Tokens.tokens;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class TokenFlowTest {
	private static final String VALUE1 = "Value1";
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";

	@Test
	void test() {
		State<WorkflowState<Value>, Value> action1 = action(STATE1, Behavior.identity());
		State<WorkflowState<Value>, Value> action2 = action(STATE2, Behavior.identity());
		
		Statemachine<WorkflowState<Value>, Value> statemachine =
			Statemachine.builder()
				.states(action1, action2)
				.transitions(
				)
				.flows(						
					tokenFlow(action1, action2)
				)
				.build();
		
		Value value1 = new Value(VALUE1);
		
		Tokens<Value> tokens = tokens(
				token(value1, action1)
		);
		WorkflowState<Value> workflowState = WorkflowState.workflowState(tokens);
		
		Data<WorkflowState<Value>, Value> dataAfter = statemachine.actOn(data(workflowState));
		Tokens<Value> tokensAfter = dataAfter.state().tokens();
		
		assertFalse(tokensAfter.isAnyTokenInState(STATE1));
		assertEquals(token(value1, action2), tokensAfter.firstTokenInState(STATE2).get());
	}
	
	private static class Value{
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
