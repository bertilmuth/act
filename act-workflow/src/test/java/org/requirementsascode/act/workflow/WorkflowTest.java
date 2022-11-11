package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;
import static org.requirementsascode.act.workflow.WorkflowApi.step;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;
import java.util.stream.Collectors;

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
	private static final String ACTION3 = "Action3";
	
	@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.actions()
			.initialActions()
			.dataFlows()
			.build();
		
		Data<WorkflowState, ActionData> afterStart = workflow.start(str(""));
		assertFalse(afterStart.value().isPresent());
		WorkflowState afterStartState = afterStart.state();
		assertTrue(tokensList(afterStartState).isEmpty());
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
		assertEquals(1, tokensList(afterAction1.state()).size());
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
		assertEquals(1, tokensList(afterAction2.state()).size());
		assertEquals(token(action2, str(ACTION2)), afterAction2.state().tokens().firstTokenIn(ACTION2).get());
	}
	
	@Test
	void testImplicitFork() {
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
		assertEquals(token(action2a, str(ACTION1)), afterAction1State.tokens().firstTokenIn(ACTION2A).get());
		assertEquals(token(action2b, str(ACTION1)), afterAction1State.tokens().firstTokenIn(ACTION2B).get());
		assertEquals(2, tokensList(afterAction1State).size());
	}
	
	@Test
	void testStepAfterImplicitFork() {
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
		WorkflowState afterForkState = workflow.nextStep(afterAction1State).state();

		assertEquals(token(action2a, str(ACTION2A)), afterForkState.tokens().firstTokenIn(ACTION2A).get());
		assertEquals(token(action2b, str(ACTION2B)), afterForkState.tokens().firstTokenIn(ACTION2B).get());
		assertEquals(2, tokensList(afterForkState).size());
	}
	
	@Test
	void testImplicitJoin() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		Action action2a = action(ACTION2A, step(StringData.class, this::action2aPerformed));
		Action action2b = action(ACTION2B, step(StringData.class, this::action2bPerformed));
		Action action3 = action(ACTION3, step(StringData.class, this::action3Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2a, action2b, action3)
			.initialActions(action1)
			.dataFlows(
				dataFlow(action1, action2a),
				dataFlow(action1, action2b),
				dataFlow(action2a, action3),
				dataFlow(action2b, action3)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();	
		WorkflowState afterForkState = workflow.nextStep(afterAction1State).state();

		assertEquals(token(action3, str(ACTION2A)), afterForkState.tokens().firstTokenIn(ACTION3).get());
		assertEquals(2, tokensList(afterForkState).size());
	}
	
	@Test
	@Disabled
	void doesntRunActionForUnknownData() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.initialActions(action1)
			.dataFlows()
			.build();
		
		WorkflowState tokensAfterStartState = workflow.start(new UnknownData()).state();
		assertTrue(tokensList(tokensAfterStartState).isEmpty());
	}
	
	@Test
	@Disabled
	void doesntRunActionForUnknownData_nextStep() {
		Action action1 = action(ACTION1, step(StringData.class, this::action1Performed));
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.initialActions(action1)
			.dataFlows()
			.build();
		
		WorkflowState afterStartState = workflow.start(new UnknownData()).state();
		WorkflowState afterNextStepState = workflow.nextStep(afterStartState).state();
		assertTrue(tokensList(afterNextStepState).isEmpty());
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
	
	private StringData action3Performed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION3);
	}
	
	private List<Token> tokensList(WorkflowState afterStartState) {
		return afterStartState.tokens().stream().collect(Collectors.toList());
	}
}

class UnknownData implements ActionData{ }
