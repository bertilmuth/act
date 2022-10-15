package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import static org.requirementsascode.act.token.Workflow.workflow;

class TokenFlowTest {
	private static final String VALUE1 = "Value1";
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";
	
	private int action1Performed = 0;
	private int action2Performed = 0;

	@Test
	void test() {
		State<Workflow<ActionData>, ActionData> action1 = action(STATE1, d -> {action1Performed++;return d;});
		State<Workflow<ActionData>, ActionData> action2 = action(STATE2, d -> {action2Performed++;return d;});
		
		Statemachine<Workflow<ActionData>, ActionData> statemachine =
			Statemachine.builder()
				.states(action1, action2)
				.transitions(
				)
				.flows(						
					tokenFlow(action1, action2)
				)
				.build();
		
		StringValue value1 = new StringValue(VALUE1);
		
		Tokens<ActionData> tokens = tokens(
				token(value1, action1)
		);
		Workflow<ActionData> workflow = workflow(tokens);
		
		Data<Workflow<ActionData>, ActionData> dataAfter = statemachine.actOn(data(workflow));
		Tokens<ActionData> tokensAfter = dataAfter.state().tokens();
		
		assertEquals(1, action1Performed);
		//assertEquals(0, action2Performed);
		assertFalse(tokensAfter.isAnyTokenInState(STATE1));
		assertEquals(token(value1, action2), tokensAfter.firstTokenInState(STATE2).get());
	}
	
	private interface ActionData{ };
	private static class StringValue implements ActionData{
		public final String string;
		public StringValue(String string) {
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
			StringValue other = (StringValue) obj;
			return Objects.equals(string, other.string);
		}
	};
}
