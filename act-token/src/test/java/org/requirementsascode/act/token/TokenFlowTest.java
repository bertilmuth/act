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
		
		StringValue startWorkflow = new StringValue(START_WORKFLOW);
		AfterStep workflowStarted = workflow.nextStep(startWorkflow);
		Tokens tokensAtStart = workflowStarted.tokens();
		assertFalse(tokensAtStart.isAnyTokenIn(ACTION2));
		assertFalse(tokensAtStart.isAnyTokenIn(ACTION3));
		assertEquals(token(action1, startWorkflow), tokensAtStart.firstTokenIn(ACTION1).get());
		
		AfterStep after1 = workflowStarted.nextStep();
		Tokens tokensAfter1 = after1.tokens();
		assertFalse(tokensAfter1.isAnyTokenIn(ACTION1));
		assertFalse(tokensAfter1.isAnyTokenIn(ACTION3));
		assertEquals(token(action2, new StringValue(ACTION1)), tokensAfter1.firstTokenIn(ACTION2).get());
		
		AfterStep after2 = after1.nextStep();
		Tokens tokensAfter2 = after2.tokens();
		assertFalse(tokensAfter1.isAnyTokenIn(ACTION1));
		assertFalse(tokensAfter2.isAnyTokenIn(ACTION2));
		assertEquals(token(action3, new StringValue(ACTION2)), tokensAfter2.firstTokenIn(ACTION3).get());
	}

	private StringValue action1Performed(Workflow workflow, StringValue input) {
		return new StringValue(ACTION1);
	}
	private StringValue action2Performed(Workflow workflow, StringValue input) {
		return new StringValue(ACTION2);
	}
	private StringValue action3Performed(Workflow workflow, StringValue input) {
		return new StringValue(ACTION3);
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
