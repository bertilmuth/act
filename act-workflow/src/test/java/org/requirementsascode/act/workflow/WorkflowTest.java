package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
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
		
		Data<WorkflowState, Token> afterStart = workflow.start(str(""));
		assertFalse(afterStart.value().isPresent());
		WorkflowState afterStartState = afterStart.state();
		assertEquals(0, nrOfTokensIn(afterStartState));
	}
	
	@Test
	void runsSingleAction() {
		Node action1 = createAction1();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		Data<WorkflowState, Token> afterAction1 = workflow.start(str(START_WORKFLOW));
		WorkflowState state = afterAction1.state();

		assertEquals(str(ACTION1), state.actionOutput().get());
	}
	
	@Test
	void runsTwoActions_firstOneUserTriggered() {
		Node action1 = createAction1();
		Node action2 = createAction2();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2)
			)
			.build();
		
		Data<WorkflowState, Token> afterAction2 = workflow.start(str(START_WORKFLOW));
		WorkflowState state = afterAction2.state();
		
		StringData action1_2 = str(ACTION1 + "." + ACTION2);
		assertEquals(action1_2, state.actionOutput().get());
		assertEquals(1, nrOfTokensIn(state));
	}
	
	@Test
	void runningActions_firstOneUserTriggered_withFalsePredicate_onlyRunsFirstAction() {
		Node action1 = createAction1();
		Node action2 = createAction2();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2, StringData.class, sd -> !sd.toString().equals(ACTION1))
			)
			.build();
		
		Data<WorkflowState, Token> afterAction2 = workflow.start(str(START_WORKFLOW));
		WorkflowState state = afterAction2.state();
		
		assertEquals(str(ACTION1), state.actionOutput().get());
		assertEquals(1, nrOfTokensIn(state));
	}
	
	@Test
	@Disabled
	void runsTwoActions_bothUserTriggered() {
		Node action1 = createAction1();
		Node action2i = createAction2i();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2i)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2i)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, Token> afterAction2i = workflow.nextStep(afterAction1State, new IntegerData(1));
		WorkflowState state = afterAction2i.state();
		
		//assertEquals(new IntegerData(2), afterAction2i.value().get());
		//assertEquals(1, tokensList(state).size());
		assertEquals(new IntegerData(2), actionDataIn(action2i, state));
	}
	
	@Test
	void testStepAfterImplicitFork() {
		Node action1 = createAction1();
		Node action2a = createAction2a();
		Node action2b = createAction2b();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2a, action2b)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2a),
				dataFlow(action1, action2b)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();	

		StringData action1_2a = str(ACTION1 + "." + ACTION2A);
		assertEquals(action1_2a, state.actionOutput().get());
		assertEquals(2, nrOfTokensIn(state));
	}
	
	@Test
	@Disabled
	void testImplicitMerge() {
		Node action1 = createAction1();
		Node action2a = createAction2a();
		Node action2b = createAction2b();
		Node action3 = createAction3();
		
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
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();	

		List<Token> tokensInAction3 = state.tokensIn(action3).collect(Collectors.toList());
		StringData expectedTokenData = str(ACTION3);
		assertEquals(expectedTokenData, tokensInAction3.get(0).actionData().get());
		assertEquals(expectedTokenData, tokensInAction3.get(1).actionData().get());
		assertEquals(2, nrOfTokensIn(state));
	}
	
	@Test
	void doesntRunActionForUnknownData() {
		Node action1 = createAction1();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState state = workflow.start(new UnknownData()).state();
		assertEquals(0, nrOfTokensIn(state));
	}
	
	private Node createAction1() {
		return action(ACTION1, StringData.class, this::action1Performed);
	}
	
	private Node createAction2() {
		return action(ACTION2, StringData.class, this::action2Performed);
	}
	
	private Node createAction2a() {
		return action(ACTION2A, StringData.class, this::action2aPerformed);
	}
	
	private Node createAction2b() {
		return action(ACTION2B, StringData.class, this::action2bPerformed);
	}
	
	private Node createAction2i() {
		return action(ACTION2I, IntegerData.class, this::action2iPerformed);
	}
	
	private Node createAction3() {
		return action(ACTION3, StringData.class, this::action3Performed);
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
	
	private long nrOfTokensIn(WorkflowState state) {
		return state.tokens().asMap().values().stream().flatMap(List::stream).count();
	}
	
	private ActionData actionDataIn(Node action1, WorkflowState state) {
		return state.firstTokenIn(action1).get().actionData().get();
	}
}

class UnknownData implements ActionData{ }
