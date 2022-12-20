package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.testdata.IntegerData;
import org.requirementsascode.act.workflow.testdata.StringData;

class WorkflowTest {
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	private static final String ACTION2A = "Action2a";
	private static final String ACTION2B = "Action2b";
	private static final String ACTION2I = "Action2i";
	private static final String ACTION3 = "Action3";
	
	@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.nodes()
			.startNodes()
			.dataFlows()
			.build();
		
		Data<WorkflowState, ActionData> afterStart = workflow.start(str(""));
		assertFalse(afterStart.value().isPresent());
		WorkflowState afterStartState = afterStart.state();
		assertTrue(tokensList(afterStartState).isEmpty());
	}
	
	@Test
	void runsSingleAction() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		Data<WorkflowState, ActionData> afterAction1 = workflow.start(str(START_WORKFLOW));
		WorkflowState state = afterAction1.state();

		assertEquals(str(ACTION1), afterAction1.value().get());
		assertEquals(1, tokensList(state).size());
		assertEquals(token(action1, str(ACTION1)), state.firstTokenIn(action1).get());
	}
	
	@Test
	void runsTwoActions_firstOneUserTriggered() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<StringData> action2 = action(ACTION2, StringData.class, this::action2Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, ActionData> afterAction2 = workflow.nextStep(afterAction1State);
		WorkflowState state = afterAction2.state();
		
		StringData action1_2 = str(ACTION1 + "." + ACTION2);
		assertEquals(action1_2, afterAction2.value().get());
		assertEquals(1, tokensList(state).size());
		assertEquals(token(action2, action1_2), state.firstTokenIn(action2).get());
	}
	
	@Test
	void runsTwoActions_firstOneUserTriggered_withTruePredicate() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<StringData> action2 = action(ACTION2, StringData.class, this::action2Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2, StringData.class, sd -> sd.toString().equals(ACTION1))
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, ActionData> afterAction2 = workflow.nextStep(afterAction1State);
		WorkflowState state = afterAction2.state();
		
		StringData action1_2 = str(ACTION1 + "." + ACTION2);
		assertEquals(action1_2, afterAction2.value().get());
		assertEquals(1, tokensList(state).size());
		assertEquals(token(action2, action1_2), state.firstTokenIn(action2).get());
	}
	
	@Test
	void runningActions_firstOneUserTriggered_withFalsePredicate_onlyRunsFirstAction() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<StringData> action2 = action(ACTION2, StringData.class, this::action2Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2, StringData.class, sd -> !sd.toString().equals(ACTION1))
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, ActionData> afterAction2 = workflow.nextStep(afterAction1State);
		WorkflowState state = afterAction2.state();
		
		assertEquals(str(ACTION1), afterAction2.value().get());
		assertEquals(1, tokensList(state).size());
		assertEquals(token(action1, str(ACTION1)), state.firstTokenIn(action1).get());
	}
	
	@Test
	void runsTwoActions_bothUserTriggered() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<IntegerData> action2i = action(ACTION2I, IntegerData.class, this::action2iPerformed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2i)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2i)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, ActionData> afterAction2i = workflow.nextStep(afterAction1State, new IntegerData(1));
		WorkflowState state = afterAction2i.state();
		
		assertEquals(new IntegerData(2), afterAction2i.value().get());
		assertEquals(1, tokensList(state).size());
		assertEquals(token(action2i, new IntegerData(2)), state.firstTokenIn(action2i).get());
	}
	
	@Test
	void testImplicitFork() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<StringData> action2a = action(ACTION2A, StringData.class, this::action2aPerformed);
		ExecutableNode<StringData> action2b = action(ACTION2B, StringData.class, this::action2bPerformed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2a, action2b)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2a),
				dataFlow(action1, action2b)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();	
		
		assertEquals(token(action2b, str(ACTION1)), state.firstTokenIn(action2b).get());
		assertEquals(2, tokensList(state).size());
	}
	
	@Test
	void testStepAfterImplicitFork() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<StringData> action2a = action(ACTION2A, StringData.class, this::action2aPerformed);
		ExecutableNode<StringData> action2b = action(ACTION2B, StringData.class, this::action2bPerformed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2a, action2b)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2a),
				dataFlow(action1, action2b)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();	
		WorkflowState state = workflow.nextStep(afterAction1State).state();

		assertEquals(token(action2a, str(ACTION1 + "." + ACTION2A)), state.firstTokenIn(action2a).get());
		assertEquals(token(action2b, str(ACTION1 + "." + ACTION2B)), state.firstTokenIn(action2b).get());
		assertEquals(2, tokensList(state).size());
	}
	
	@Test
	void testImplicitMerge() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		ExecutableNode<StringData> action2a = action(ACTION2A, StringData.class, this::action2aPerformed);
		ExecutableNode<StringData> action2b = action(ACTION2B, StringData.class, this::action2bPerformed);
		ExecutableNode<StringData> action3 = action(ACTION3, StringData.class, this::action3Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2a, action2b, action3)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2a),
				dataFlow(action1, action2b),
				dataFlow(action2a, action3),
				dataFlow(action2b, action3)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();	
		WorkflowState state = workflow.nextStep(afterAction1State).state();

		assertEquals(token(action3, str(ACTION1 + "." + ACTION2A)), state.firstTokenIn(action3).get());
		assertEquals(2, tokensList(state).size());
	}
	
	@Test
	void doesntRunActionForUnknownData() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState state = workflow.start(new UnknownData()).state();
		assertTrue(tokensList(state).isEmpty());
	}
	
	@Test
	void doesntRunActionForUnknownData_nextStep() {
		ExecutableNode<StringData> action1 = action(ACTION1, StringData.class, this::action1Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState afterStartState = workflow.start(new UnknownData()).state();
		WorkflowState state = workflow.nextStep(afterStartState).state();
		assertTrue(tokensList(state).isEmpty());
	}

	private StringData str(String str) {
		return new StringData(str);
	}
	
	private StringData action1Performed(WorkflowState state, StringData input) {
		return new StringData(ACTION1);
	}
	
	private StringData action2Performed(WorkflowState state, StringData input) {
		return previousValuePlus(ACTION2, state);
	}
	
	private StringData action2aPerformed(WorkflowState state, StringData input) {
		return previousValuePlus(ACTION2A, state);
	}
	
	private StringData action2bPerformed(WorkflowState state, StringData input) {
		return previousValuePlus(ACTION2B, state);
	}
	
	private IntegerData action2iPerformed(WorkflowState state, IntegerData input) {
		return new IntegerData(input.integer + 1);
	}
	
	private StringData action3Performed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION3);
	}
	
	private StringData previousValuePlus(String actionNameToBeAdded, WorkflowState workflowState) {
		String previousValue = workflowState.actionOutput().map(Object::toString).orElse("");
		StringData concatenatedStringValue = new StringData(previousValue + "." + actionNameToBeAdded);
		return concatenatedStringValue;
	}
	
	private List<Token> tokensList(WorkflowState state) {
		return state.tokens().stream().collect(Collectors.toList());
	}
}

class UnknownData implements ActionData{ }
