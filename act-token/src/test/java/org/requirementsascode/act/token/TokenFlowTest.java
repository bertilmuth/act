package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Actions.actions;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;
import static org.requirementsascode.act.token.Tokens.tokens;
import static org.requirementsascode.act.token.Workflow.workflow;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.Workflow.AfterStep;

class TokenFlowTest {
	private static final String VALUE1 = "Value1";
	private static final String STATE1 = "State1";
	private static final String STATE2 = "State2";
	private static final String STATE3 = "State3";
	
	private int action1Performed = 0;
	private int action2Performed = 0;
	private int action3Performed = 0;

	@Test
	void runTwoWorkflowSteps() {
		Action action1 = action(STATE1, when(StringValue.class, this::action1Performed));
		Action action2 = action(STATE2, when(StringValue.class, this::action2Performed));
		Action action3 = action(STATE3, when(StringValue.class, this::action3Performed));
		
		StringValue actionData1 = new StringValue(VALUE1);
		
		AfterStep afterStep1 = workflow(
				tokens(token(action1, actionData1)), 
				actions(asList(action1,action2,action3)), 
				asList(
					tokenFlow(action1, action2),
					tokenFlow(action2, action3)
				)
			)
			.nextStep();
		
		Tokens tokens1 = afterStep1.tokens();
		
		assertEquals(1, action1Performed);
		assertEquals(0, action2Performed);
		assertEquals(0, action3Performed);
		assertFalse(tokens1.areSufficientForNode(STATE1));
		assertEquals(token(action2, actionData1), tokens1.firstTokenInNode(STATE2).get());
		
		AfterStep afterStep2 = afterStep1.nextStep();
		Tokens tokensAfterStep2 = afterStep2.tokens();

		assertEquals(1, action1Performed);
		assertEquals(1, action2Performed);
		assertEquals(0, action3Performed);
		assertFalse(tokensAfterStep2.areSufficientForNode(STATE2));
		assertEquals(token(action3, actionData1), tokensAfterStep2.firstTokenInNode(STATE3).get());
	}

	private Data<Workflow,StringValue> action1Performed(Data<Workflow,StringValue> inputData) {
		action1Performed++;
		return inputData;
	}
	private Data<Workflow,StringValue> action2Performed(Data<Workflow,StringValue> inputData) {
		action2Performed++;
		return inputData;
	}
	private Data<Workflow,StringValue> action3Performed(Data<Workflow,StringValue> inputData) {
		action3Performed++;
		return inputData;
	}
	
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
		@Override
		public String toString() {
			return "StringValue [" + string + "]";
		}
	};
}
