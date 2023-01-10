package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;
import static org.requirementsascode.act.workflow.WorkflowApi.port;

import java.util.List;
import java.util.function.BiFunction;
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
	
	private static final String _IN = "_In";
	private static final String _OUT = "_OUT";
	
	private static final String ACTION1_IN = ACTION1 + _IN;
	
	private static final String ACTION2_IN = ACTION2 + _IN;
	
	private static final String ACTION2A_IN = ACTION2A + _IN;
	
	private static final String ACTION2B_IN = ACTION2B + _IN;
	
	private static final String ACTION2I_IN = ACTION2I + _IN;
	
	private static final String ACTION3_IN = ACTION3 + _IN;
	
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
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Workflow workflow = Workflow.builder()
			.nodes(port1, action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		Data<WorkflowState, Token> afterAction1 = workflow.start(str(START_WORKFLOW));
		WorkflowState state = afterAction1.state();

		assertEquals(str(ACTION1), state.actionOutput().get());
	}
	
	@Test
	void runsTwoActions_firstOneUserTriggered() {
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<StringData> port2 = port(ACTION2_IN, StringData.class);
		Node action2 = createAction2(port2);
		
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
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<StringData> port2 = port(ACTION2_IN, StringData.class);
		Node action2 = createAction2(port2);
		
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
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<IntegerData> port2i = port(ACTION2I_IN, IntegerData.class);
		Node action2i = createAction2i(port2i);
		
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
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<StringData> port2a = port(ACTION2A_IN, StringData.class);
		Node action2a = createAction2a(port2a);
		
		Port<StringData> port2b = port(ACTION2B_IN, StringData.class);
		Node action2b = createAction2b(port2b);
		
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
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<StringData> port2a = port(ACTION2A_IN, StringData.class);
		Node action2a = createAction2a(port2a);
		
		Port<StringData> port2b = port(ACTION2B_IN, StringData.class);
		Node action2b = createAction2b(port2b);
		
		Port<StringData> port3 = port(ACTION3_IN, StringData.class);
		Node action3 = createAction3(port3);
		
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
		Port<StringData> port1 = port(ACTION1_IN, StringData.class);
		Node action1 = createAction1(port1);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState state = workflow.start(new UnknownData()).state();
		assertEquals(0, nrOfTokensIn(state));
	}
	
	private Node createAction1(Port<StringData> port1) {
		return createAction(port1, port1, ACTION1, this::action1Performed);
	}
	
	private Node createAction2(Port<StringData> port2) {
		return createAction(port2, port2, ACTION2, this::action2Performed);
	}
	
	private Node createAction2a(Port<StringData> port2a) {
		return createAction(port2a, port2a, ACTION2A, this::action2aPerformed);
	}
	
	private Node createAction2b(Port<StringData> port2b) {
		return createAction(port2b, port2b, ACTION2B, this::action2bPerformed);
	}
	
	private Node createAction2i(Port<IntegerData> port2i) {
		return createAction(port2i, port2i, ACTION2I, this::action2iPerformed);
	}
	
	private Node createAction3(Port<StringData> port3) {
		return createAction(port3, port3, ACTION3, this::action3Performed);
	}
	
	private <T extends ActionData, U extends ActionData> Node createAction(Port<T> inputPort, Port<U> outputPort, String actionName, BiFunction<WorkflowState, T, U> actionFunction) {
		return action(actionName, inputPort, outputPort, actionFunction);
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
