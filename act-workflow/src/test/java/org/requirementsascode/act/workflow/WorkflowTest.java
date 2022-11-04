package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;
import static org.requirementsascode.act.workflow.WorkflowApi.step;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.testdata.StringData;

class WorkflowTest {
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	private static final String ACTION2A = "Action2a";
	private static final String ACTION2B = "Action2b";
	
	@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.actions()
			.initialActions()
			.dataFlows()
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
			.initialActions(action1)
			.dataFlows()
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
			.initialActions(action1)
			.dataFlows(
				dataFlow(action1, action2)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, ActionData> afterAction2 = workflow.nextStep(afterAction1State);
		
		assertEquals(str(ACTION2), afterAction2.value().get());
		assertEquals(1, afterAction2.state().tokens().stream().toList().size());
		assertEquals(token(action2, str(ACTION2)), afterAction2.state().tokens().firstTokenIn(ACTION2).get());
	}
	
	@Test
	void testFork() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		Action action2a = action(ACTION2A, step(StringData.class, this::action2aPerformed));
		Action action2b = action(ACTION2B, step(StringData.class, this::action2bPerformed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2a, action2b)
			.initialActions(action1)
			.dataFlows(
				dataFlow(action1, action2a),
				dataFlow(action1, action2b)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		System.out.println(afterAction1State.tokens().stream().toList());
		
		Data<WorkflowState, ActionData> afterAction2 = workflow.nextStep(afterAction1State);
		
		//assertEquals(str(ACTION2), afterAction2.value().get());
		//assertEquals(2, afterAction2.state().tokens().stream().toList().size());
		//System.out.println(afterAction2.state().tokens().stream().toList());
		//assertEquals(token(action2a, str(ACTION2)), afterAction2.state().tokens().firstTokenIn(ACTION2).get());
	}
	
	@Test
	void doesntRunActionForUnknownData() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.initialActions(action1)
			.dataFlows()
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
			.initialActions(action1)
			.dataFlows()
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
	
	private StringData action2aPerformed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION2A);
	}
	
	private StringData action2bPerformed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION2B);
	}
}

class UnknownData implements ActionData{ }
