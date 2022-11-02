package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Step.step;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import java.util.List;

import org.junit.jupiter.api.Test;

class WorkflowTest {
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	
	@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.actions()
			.tokenFlows()
			.initialActions()
			.build();
		
		WorkflowState afterStart = workflow.start(s(""));
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
		
		WorkflowState afterStart = workflow.start(s(START_WORKFLOW));
		assertEquals(new StringData(ACTION1), afterStart.actionOutput().get());
		assertEquals(token(action1, s(ACTION1)), afterStart.tokens().firstTokenIn(ACTION1).get());
	}
	
	@Test
	void runsTwoActions() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		Action action2 = action(ACTION2, step(StringData.class, this::action2Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2)
			.tokenFlows(
				tokenFlow(action1, action2)
			)
			.initialActions(action1)
			.build();
		
		WorkflowState afterStart = workflow.start(s(START_WORKFLOW));
		WorkflowState afterAction1 = workflow.nextStep(data(afterStart, Step.proceed));
		assertEquals(new StringData(ACTION2), afterAction1.actionOutput().get());
		assertEquals(token(action2, s(ACTION2)), afterAction1.tokens().firstTokenIn(ACTION2).get());
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
		
		WorkflowState afterStart = workflow.start(new UnknownData());
		Tokens tokensAfterNextStep = workflow.nextStep(data(afterStart, Step.proceed)).tokens();
		List<Token> tokenList = tokensAfterNextStep.stream().toList();
		assertTrue(tokenList.isEmpty());
	}

	private StringData s(String str) {
		return new StringData(str);
	}
	
	private StringData action1Performed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION1);
	}
	
	private StringData action2Performed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION2);
	}
}

class UnknownData implements ActionData{ }
