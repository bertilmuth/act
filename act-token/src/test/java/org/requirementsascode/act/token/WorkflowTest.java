package org.requirementsascode.act.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.token.Action.action;
import static org.requirementsascode.act.token.Step.step;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;

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
		
		Data<WorkflowState, ActionData> afterStart = workflow.start(str(""));
		assertTrue(afterStart.value().isEmpty());
		assertTrue(afterStart.state().tokens().stream().toList().isEmpty());
	}
	
	@Test
	void runsSingleAction() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		Data<WorkflowState, ActionData> afterAction1 = workflow.start(str(START_WORKFLOW));
		
		assertEquals(str(ACTION1), afterAction1.value().get());
		assertEquals(1, afterAction1.state().tokens().stream().toList().size());
		assertEquals(token(action1, str(ACTION1)), afterAction1.state().tokens().firstTokenIn(ACTION1).get());
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
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, ActionData> afterAction2 = workflow.nextStep(afterAction1State);
		
		assertEquals(str(ACTION2), afterAction2.value().get());
		assertEquals(1, afterAction2.state().tokens().stream().toList().size());
		assertEquals(token(action2, str(ACTION2)), afterAction2.state().tokens().firstTokenIn(ACTION2).get());
	}
	
	@Test
	void doesntRunActionForUnknownData() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.tokenFlows()
			.initialActions(action1)
			.build();
		
		Tokens tokensAfterStart = workflow.start(new UnknownData()).state().tokens();
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
		
		WorkflowState afterStartState = workflow.start(new UnknownData()).state();
		Tokens tokensAfterNextStep = workflow.nextStep(afterStartState).state().tokens();
		List<Token> tokenList = tokensAfterNextStep.stream().toList();
		assertTrue(tokenList.isEmpty());
	}

	private StringData str(String str) {
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
