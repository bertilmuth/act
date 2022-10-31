package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Step.step;
import static org.requirementsascode.act.token.Token.token;

import java.util.List;

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
		
		AfterStep afterStart = workflow.start(s(""));
		assertTrue(afterStart.actionOutput().isEmpty());
		assertTrue(afterStart.tokens().stream().toList().isEmpty());
	}
	
	@Test
	void runsSingleAction() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		AfterStep afterStart = workflow.start(s(START_WORKFLOW));
		Tokens tokensAtStart = afterStart.tokens();
		assertEquals(token(action1, s(ACTION1)), tokensAtStart.firstTokenIn(ACTION1).get());
	}
	
	@Test
	void doesntRunActionForUnknownData() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		Tokens tokensAfterStart = workflow.start(new UnknownData()).tokens();
		List<Token> tokenList = tokensAfterStart.stream().toList();
		assertTrue(tokenList.isEmpty());
	}
	
	@Test
	void doesntRunActionForUnknownData_nextStep() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		Tokens tokensAfterNextStep = workflow.start(new UnknownData()).nextStep().tokens();
		List<Token> tokenList = tokensAfterNextStep.stream().toList();
		assertTrue(tokenList.isEmpty());
	}

	private StringData s(String str) {
		return new StringData(str);
	}
	
	private StringData action1Performed(Workflow workflow, StringData input) {
		return new StringData(ACTION1);
	}
}

class UnknownData implements ActionData{ }
