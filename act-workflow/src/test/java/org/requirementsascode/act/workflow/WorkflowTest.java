package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.flow;
import static org.requirementsascode.act.workflow.WorkflowApi.port;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
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
	
	private static final String _IN = "_IN";
	private static final String _OUT = "_OUT";
	
	private static final String ACTION1_IN = ACTION1 + _IN;
	private static final String ACTION1_OUT = ACTION1 + _OUT;
	
	private static final String ACTION2_IN = ACTION2 + _IN;
	private static final String ACTION2_OUT = ACTION2 + _OUT;
	
	private static final String ACTION2A_IN = ACTION2A + _IN;
	private static final String ACTION2A_OUT = ACTION2A + _OUT;
	
	private static final String ACTION2B_IN = ACTION2B + _IN;
	private static final String ACTION2B_OUT = ACTION2B + _OUT;
	
	private static final String ACTION2I_IN = ACTION2I + _IN;
	private static final String ACTION2I_OUT = ACTION2I + _OUT;
	
	private static final String ACTION3_IN = ACTION3 + _IN;
	private static final String ACTION3_OUT = ACTION3 + _OUT;
	
	@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.actions()
			.ports()
			.startPorts()
			.flows()
			.build();
		
		WorkflowState state = workflow.start(str(""));
		assertEquals(0, nrOfTokensInState(state));
	}
	
	@Test
	void runsSingleAction() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		ActionNode<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.ports(action1_In, action1_Out)
			.startPorts(action1_In)
			.flows()
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW));
		assertEquals(str(ACTION1), action1_Out.firstActionData(state).get());
	}
	
	@Test
	void runsTwoActions_firstOneUserTriggered() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		ActionNode<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2_In = port(ACTION2_IN, StringData.class);
		Port<StringData> action2_Out = port(ACTION2_OUT, StringData.class);
		ActionNode<StringData, StringData> action2 = createAction2(action2_In, action2_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2)
			.ports(
				action1_In,action1_Out,
				action2_In, action2_Out
			)
			.startPorts(action1_In)
			.flows(
				flow(action1_Out, action2_In)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW));
		
		assertEquals(str(ACTION1 + "." + ACTION2), action2_Out.firstActionData(state).get());
		assertEquals(1, nrOfTokensInState(state));
	}
	
	/*@Test
	@Disabled
	void runsTwoActions_bothUserTriggered() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		ActionNode<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<IntegerData> action2i_In = port(ACTION2I_IN, IntegerData.class);
		Port<IntegerData> action2i_Out = port(ACTION2I_OUT, IntegerData.class);
		ActionNode<IntegerData, IntegerData> action2i = createAction2i(action2i_In, action2i_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2i)
			.ports(
				action1_In, action1_Out,
				action2i_In, action2i_Out
			)
			.startPorts(action1_In)
			.flows(
				flow(action1_Out, action2i_In)
			)
			.build();
		
		WorkflowState afterAction1State = workflow.start(str(START_WORKFLOW)).state();
		Data<WorkflowState, Token> afterAction2i = workflow.nextStep(afterAction1State, new IntegerData(1));
		WorkflowState state = afterAction2i.state();
		
		//assertEquals(new IntegerData(2), afterAction2i.value().get());
		//assertEquals(1, tokensList(state).size());
		assertEquals(new IntegerData(2), actionDataIn(action2i, state));
	}*/
	
	@Test
	void testStepAfterImplicitFork() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		ActionNode<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2a_In = port(ACTION2A_IN, StringData.class);
		Port<StringData> action2a_Out = port(ACTION2A_OUT, StringData.class);
		ActionNode<StringData, StringData> action2a = createAction2a(action2a_In, action2a_Out);
		
		Port<StringData> action2b_In = port(ACTION2B_IN, StringData.class);
		Port<StringData> action2b_Out = port(ACTION2B_OUT, StringData.class);
		ActionNode<StringData, StringData> action2b = createAction2b(action2b_In, action2b_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2a, action2b)
			.ports(
				action1_In, action1_Out,
				action2a_In, action2a_Out,
				action2b_In, action2b_Out
			)
			.startPorts(action1_In)
			.flows(
				flow(action1_Out, action2a_In),
				flow(action1_Out, action2b_In)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW));	

		assertEquals(str(ACTION1 + "." + ACTION2A), action2a_Out.firstActionData(state).get());
		assertEquals(str(ACTION1 + "." + ACTION2B), action2b_Out.firstActionData(state).get());
		assertEquals(2, nrOfTokensInState(state));
	}
	
	@Test
	void testImplicitMerge() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		ActionNode<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2a_In = port(ACTION2A_IN, StringData.class);
		Port<StringData> action2a_Out = port(ACTION2A_OUT, StringData.class);
		ActionNode<StringData, StringData> action2a = createAction2a(action2a_In, action2a_Out);
		
		Port<StringData> action2b_In = port(ACTION2B_IN, StringData.class);
		Port<StringData> action2b_Out = port(ACTION2B_OUT, StringData.class);
		ActionNode<StringData, StringData> action2b = createAction2b(action2b_In, action2b_Out);
		
		Port<StringData> action3_In = port(ACTION3_IN, StringData.class);
		Port<StringData> action3_Out = port(ACTION3_OUT, StringData.class);
		ActionNode<StringData, StringData> action3 = createAction3(action3_In, action3_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2a, action2b, action3)
			.ports(
				action1_In, action1_Out,
				action2a_In, action2a_Out,
				action2b_In, action2b_Out,
				action3_In, action3_Out
			)
			.startPorts(action1_In)
			.flows(
				flow(action1_Out, action2a_In),
				flow(action1_Out, action2b_In),
				flow(action2a_Out, action3_In),
				flow(action2b_Out, action3_In)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW));	

		List<ActionData> tokensInAction3Out = action3_Out.allActionData(state).collect(Collectors.toList());
		assertEquals(str(ACTION3), tokensInAction3Out.get(0));
		assertEquals(str(ACTION3), tokensInAction3Out.get(1));
		assertEquals(2, nrOfTokensInState(state));
	}
	
	@Test
	void doesntRunActionForUnknownData() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		ActionNode<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.ports(action1_In, action1_Out)
			.startPorts(action1_In)
			.flows()
			.build();
		
		WorkflowState state = workflow.start(new UnknownData());
		assertEquals(0, nrOfTokensInState(state));
	}
	
	private ActionNode<StringData,StringData> createAction1(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION1, inputPort, outputPort, this::action1Performed);
	}
	
	private ActionNode<StringData,StringData> createAction2(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION2, inputPort, outputPort, this::action2Performed);
	}
	
	private ActionNode<StringData,StringData> createAction2a(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION2A, inputPort, outputPort, this::action2aPerformed);
	}
	
	private ActionNode<StringData,StringData> createAction2b(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION2B, inputPort, outputPort, this::action2bPerformed);
	}
	
	private ActionNode<IntegerData,IntegerData> createAction2i(Port<IntegerData> inputPort, Port<IntegerData> outputPort) {
		return createAction(ACTION2I, inputPort, outputPort, this::action2iPerformed);
	}
	
	private ActionNode<StringData,StringData> createAction3(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION3, inputPort, outputPort, this::action3Performed);
	}
	
	private <T extends ActionData, U extends ActionData> ActionNode<T,U> createAction(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
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
	
	private long nrOfTokensInState(WorkflowState state) {
		return state.tokens().asMap().values().stream().flatMap(List::stream).count();
	}
}

class UnknownData implements ActionData{ }
