package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.token.Workflow.AfterStep;

class WorkflowTest {

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
}
