package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.function.Atomic.atomic;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.token.Workflow.AfterStep;

class WorkflowTest {
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	
	@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.actions()
			.tokenFlows()
			.initialActions()
			.build();
		
		AfterStep workflowStarted = workflow.start(s(""));
		assertTrue(workflowStarted.actionOutput().isEmpty());
		assertTrue(workflowStarted.tokens().stream().toList().isEmpty());
	}
	
	@Test
	void runsSingleAction() {
		Action action1 = action(ACTION1, atomic(StringValue.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		Tokens tokensAtStart = workflow.start(s(START_WORKFLOW)).tokens();
		assertEquals(token(action1, s(START_WORKFLOW)), tokensAtStart.firstTokenIn(ACTION1).get());

		Tokens tokensAfter1 = workflow.start(s(START_WORKFLOW)).nextStep().tokens();
		assertEquals(token(action1, s(ACTION1)), tokensAfter1.firstTokenIn(ACTION1).get());
	}

	private StringValue s(String stringValue) {
		return new StringValue(stringValue);
	}
	
	private StringValue action1Performed(Workflow workflow, StringValue input) {
		return new StringValue(ACTION1);
	}
}
