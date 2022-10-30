package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		StringValue emptyString = new StringValue("");
		AfterStep workflowStarted = workflow.nextStep(emptyString);
		
		assertTrue(workflowStarted.actionOutput().isEmpty());
		assertFalse(workflowStarted.tokens().isAnyTokenIn(""));
	}
	
	@Test
	void runsSingleAction() {
		Action action1 = action(ACTION1, atomic(StringValue.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		StringValue startWorkflow = new StringValue(START_WORKFLOW);
		AfterStep workflowStarted = workflow.nextStep(startWorkflow);
		Tokens tokensAtStart = workflowStarted.tokens();
		assertEquals(token(action1, startWorkflow), tokensAtStart.firstTokenIn(ACTION1).get());

		AfterStep after1 = workflowStarted.nextStep();
		Tokens tokensAfter1 = after1.tokens();
		assertEquals(token(action1, new StringValue(ACTION1)), tokensAfter1.firstTokenIn(ACTION1).get());
	}
	
	private StringValue action1Performed(Workflow workflow, StringValue input) {
		return new StringValue(ACTION1);
	}
}
