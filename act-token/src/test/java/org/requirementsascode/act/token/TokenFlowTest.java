package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Step.step;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.token.Workflow.AfterStep;

class TokenFlowTest {
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	private static final String ACTION3 = "Action3";

	@Test
	void runTwoWorkflowSteps() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		Action action2 = action(ACTION2, step(StringData.class, this::action2Performed));
		Action action3 = action(ACTION3, step(StringData.class, this::action3Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1,action2,action3)
			.tokenFlows(
				tokenFlow(action1, action2),
				tokenFlow(action2, action3)
			) 
			.initialActions(action1)
			.build();
		
		StringData startData = new StringData(START_WORKFLOW);
		AfterStep workflowStarted = workflow.start(startData);
		Tokens tokensAtStart = workflowStarted.tokens();
		assertFalse(tokensAtStart.isAnyTokenIn(ACTION1));
		assertFalse(tokensAtStart.isAnyTokenIn(ACTION3));
		assertEquals(token(action2, new StringData(ACTION1)), tokensAtStart.firstTokenIn(ACTION2).get());
		
		Tokens tokensAfter2 = workflowStarted.nextStep().tokens();
		assertFalse(tokensAfter2.isAnyTokenIn(ACTION1));
		assertFalse(tokensAfter2.isAnyTokenIn(ACTION2));
		assertEquals(token(action3, new StringData(ACTION2)), tokensAfter2.firstTokenIn(ACTION3).get());
	}

	private StringData action1Performed(Workflow workflow, StringData input) {
		return new StringData(ACTION1);
	}
	private StringData action2Performed(Workflow workflow, StringData input) {
		return new StringData(ACTION2);
	}
	private StringData action3Performed(Workflow workflow, StringData input) {
		return new StringData(ACTION3);
	}
}
