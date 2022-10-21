package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;
import static org.requirementsascode.act.token.function.Atomic.atomic;
import static org.requirementsascode.act.token.function.SystemFunction.systemFunction;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.token.Workflow.AfterStep;

class TokenFlowTest {
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	private static final String ACTION3 = "Action3";
	
	private int action1Performed = 0;
	private int action2Performed = 0;
	private int action3Performed = 0;

	@Test
	void runTwoWorkflowSteps() {
		Action action1 = action(ACTION1, atomic(systemFunction(StringValue.class, this::action1Performed)));
		Action action2 = action(ACTION2, atomic(systemFunction(StringValue.class, this::action2Performed)));
		Action action3 = action(ACTION3, atomic(systemFunction(StringValue.class, this::action3Performed)));
		
		Workflow workflow = Workflow.builder()
			.actions(action1,action2,action3)
			.tokenFlows(
				tokenFlow(action1, action2),
				tokenFlow(action2, action3)
			) 
			.initialActions(action1)
			.build();
		
		StringValue actionData1 = new StringValue(START_WORKFLOW);
		AfterStep initialStep = workflow.nextStep(actionData1);
		AfterStep afterStep1 = initialStep.nextStep();
		
		Tokens tokens1 = afterStep1.tokens();
		
		assertEquals(1, action1Performed);
		assertEquals(0, action2Performed);
		assertEquals(0, action3Performed);
		assertFalse(tokens1.isAnyTokenIn(ACTION1));
		assertEquals(token(action2, actionData1), tokens1.firstTokenIn(ACTION2).get());
		
		AfterStep afterStep2 = afterStep1.nextStep();
		Tokens tokensAfterStep2 = afterStep2.tokens();

		assertEquals(1, action1Performed);
		assertEquals(1, action2Performed);
		assertEquals(0, action3Performed);
		assertFalse(tokensAfterStep2.isAnyTokenIn(ACTION2));
		assertEquals(token(action3, actionData1), tokensAfterStep2.firstTokenIn(ACTION3).get());
	}

	private StringValue action1Performed(Workflow workflow, StringValue input) {
		action1Performed++;
		return input;
	}
	private StringValue action2Performed(Workflow workflow, StringValue input) {
		action2Performed++;
		return input;
	}
	private StringValue action3Performed(Workflow workflow, StringValue input) {
		action3Performed++;
		return input;
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
