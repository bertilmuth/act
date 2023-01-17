package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.flow;
import static org.requirementsascode.act.workflow.WorkflowApi.port;
import static org.requirementsascode.act.workflow.WorkflowApi.ports;

import java.util.List;
import java.util.Set;
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
	
	private static final String ACTION2I_STR_IN = ACTION2I + "_Str" + _IN;
	private static final String ACTION2I_INT_IN = ACTION2I + "_Int" + _IN;
	private static final String ACTION2I_OUT = ACTION2I + _OUT;
	
	private static final String ACTION3_IN = ACTION3 + _IN;
	private static final String ACTION3_OUT = ACTION3 + _OUT;
	
	/*@Test
	void runningEmptyWorkflowDoesNothing() {
		Workflow workflow = Workflow.builder()
			.actions()
			.ports()
			.inPorts()
			.flows()
			.build();
		
		WorkflowState state = workflow.start(str(""));
		assertEquals(0, nrOfTokensInState(state));
	}*/
	
	@Test
	void runsSingleAction() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		Action<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1)
			.ports(action1_In, action1_Out)
			.inPorts(action1_In)
			.flows()
			.build();
		
		WorkflowState state = workflow.enterInitialData(action1_In, str(START_WORKFLOW));
		assertEquals(str(ACTION1), action1_Out.firstActionData(state).get());
	}
	
	@Test
	void runsTwoActions_firstOneUserTriggered() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		Action<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2_In = port(ACTION2_IN, StringData.class);
		Port<StringData> action2_Out = port(ACTION2_OUT, StringData.class);
		Action<StringData, StringData> action2 = createAction2(action2_In, action2_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2)
			.ports(
				action1_In,action1_Out,
				action2_In, action2_Out
			)
			.inPorts(action1_In)
			.flows(
				flow(action1_Out, action2_In)
			)
			.build();
		
		WorkflowState state1 = workflow.enterInitialData(action1_In, str(START_WORKFLOW));
		WorkflowState state2 = workflow.next(state1);
		
		assertEquals(str(ACTION1 + "." + ACTION2), action2_Out.firstActionData(state2).get());
		assertEquals(1, nrOfTokensInState(state2));
	}
	
	@Test
	void runsTwoActions_bothUserTriggered() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		Action<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2i_Str_In = port(ACTION2I_STR_IN, StringData.class);
		Port<IntegerData> action2i_Int_In = port(ACTION2I_INT_IN, IntegerData.class);
		Port<IntegerData> action2i_Out = port(ACTION2I_OUT, IntegerData.class);
		Action<IntegerData, IntegerData> action2i = createAction2i(action2i_Str_In, action2i_Int_In, action2i_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2i)
			.ports(
				action1_In, action1_Out,
				action2i_Str_In, action2i_Int_In, action2i_Out
			)
			.inPorts(action1_In, action2i_Int_In)
			.flows(
				flow(action1_Out, action2i_Str_In)
			)
			.build();
		
		WorkflowState afterAction1 = workflow.enterInitialData(action1_In, str(START_WORKFLOW));
		WorkflowState state = workflow.enterData(afterAction1, action2i_Int_In, new IntegerData(1));
		
		assertEquals(2, action2i_Out.firstActionData(state).get().integer);
		assertEquals(1, nrOfTokensInState(state));
	}
	
	@Test
	void testStepAfterImplicitFork() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		Action<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2a_In = port(ACTION2A_IN, StringData.class);
		Port<StringData> action2a_Out = port(ACTION2A_OUT, StringData.class);
		Action<StringData, StringData> action2a = createAction2a(action2a_In, action2a_Out);
		
		Port<StringData> action2b_In = port(ACTION2B_IN, StringData.class);
		Port<StringData> action2b_Out = port(ACTION2B_OUT, StringData.class);
		Action<StringData, StringData> action2b = createAction2b(action2b_In, action2b_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2a, action2b)
			.ports(
				action1_In, action1_Out,
				action2a_In, action2a_Out,
				action2b_In, action2b_Out
			)
			.inPorts(action1_In)
			.flows(
				flow(action1_Out, action2a_In),
				flow(action1_Out, action2b_In)
			)
			.build();
		
		WorkflowState state1 = workflow.enterInitialData(action1_In, str(START_WORKFLOW));	
		WorkflowState state2 = workflow.next(state1);

		assertEquals(str(ACTION1 + "." + ACTION2A), action2a_Out.firstActionData(state2).get());
		assertEquals(str(ACTION1 + "." + ACTION2B), action2b_Out.firstActionData(state2).get());
		assertEquals(2, nrOfTokensInState(state2));
	}
	
	@Test
	void testImplicitMerge() {
		Port<StringData> action1_In = port(ACTION1_IN, StringData.class);
		Port<StringData> action1_Out = port(ACTION1_OUT, StringData.class);
		Action<StringData, StringData> action1 = createAction1(action1_In, action1_Out);
		
		Port<StringData> action2a_In = port(ACTION2A_IN, StringData.class);
		Port<StringData> action2a_Out = port(ACTION2A_OUT, StringData.class);
		Action<StringData, StringData> action2a = createAction2a(action2a_In, action2a_Out);
		
		Port<StringData> action2b_In = port(ACTION2B_IN, StringData.class);
		Port<StringData> action2b_Out = port(ACTION2B_OUT, StringData.class);
		Action<StringData, StringData> action2b = createAction2b(action2b_In, action2b_Out);
		
		Port<StringData> action3_In = port(ACTION3_IN, StringData.class);
		Port<StringData> action3_Out = port(ACTION3_OUT, StringData.class);
		Action<StringData, StringData> action3 = createAction3(action3_In, action3_Out);
		
		Workflow workflow = Workflow.builder()
			.actions(action1, action2a, action2b, action3)
			.ports(
				action1_In, action1_Out,
				action2a_In, action2a_Out,
				action2b_In, action2b_Out,
				action3_In, action3_Out
			)
			.inPorts(action1_In)
			.flows(
				flow(action1_Out, action2a_In),
				flow(action1_Out, action2b_In),
				flow(action2a_Out, action3_In),
				flow(action2b_Out, action3_In)
			)
			.build();
		
		WorkflowState state1 = workflow.enterInitialData(action1_In, str(START_WORKFLOW));	
		WorkflowState state2 = workflow.next(state1);
		WorkflowState state3 = workflow.next(state2);
		WorkflowState state4 = workflow.next(state3);

		List<ActionData> tokensInAction3Out = action3_Out.allActionData(state4).collect(Collectors.toList());
		assertEquals(str(ACTION3), tokensInAction3Out.get(0));
		assertEquals(2, nrOfTokensInState(state2));
	}
	
	private Action<StringData,StringData> createAction1(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION1, StringData.class, inputPort, outputPort, this::action1Performed);
	}
	
	private Action<StringData,StringData> createAction2(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION2, StringData.class, inputPort, outputPort, this::action2Performed);
	}
	
	private Action<StringData,StringData> createAction2a(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION2A, StringData.class, inputPort, outputPort, this::action2aPerformed);
	}
	
	private Action<StringData,StringData> createAction2b(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION2B, StringData.class, inputPort, outputPort, this::action2bPerformed);
	}
	
	private Action<IntegerData,IntegerData> createAction2i(Port<StringData> strInputPort, Port<IntegerData> intInPort, Port<IntegerData> outputPort) {
		return createAction(ACTION2I, IntegerData.class, ports(strInputPort, intInPort), ports(outputPort), this::action2iPerformed);
	}
	
	private Action<StringData,StringData> createAction3(Port<StringData> inputPort, Port<StringData> outputPort) {
		return createAction(ACTION3, StringData.class, inputPort, outputPort, this::action3Performed);
	}
	
	private <T extends ActionData, U extends ActionData> Action<T,U> createAction(String actionName, Class<T> actionType, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		return action(actionName, actionType, inputPort, outputPort, actionFunction);
	}
	
	private <T extends ActionData, U extends ActionData> Action<T,U> createAction(String actionName, Class<T> actionType, Ports inputPorts, Ports outputPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		return action(actionName, actionType, inputPorts, outputPorts, actionFunction);
	}

	private StringData str(String str) {
		return new StringData(str);
	}
	
	private StringData action1Performed(WorkflowState state, StringData input) {
		return new StringData(ACTION1);
	}
	
	private StringData action2Performed(WorkflowState state, StringData input) {
		return previousValuePlus(input, ACTION2);
	}
	
	private StringData action2aPerformed(WorkflowState state, StringData input) {
		return previousValuePlus(input, ACTION2A);
	}
	
	private StringData action2bPerformed(WorkflowState state, StringData input) {
		return previousValuePlus(input, ACTION2B);
	}
	
	private IntegerData action2iPerformed(WorkflowState state, IntegerData input) {
		return new IntegerData(input.integer + 1);
	}
	
	private StringData action3Performed(WorkflowState workflowState, StringData input) {
		return new StringData(ACTION3);
	}
	
	private StringData previousValuePlus(StringData stringData, String actionNameToBeAdded) {
		String previousValue = stringData.string;
		StringData concatenatedStringValue = new StringData(previousValue + "." + actionNameToBeAdded);
		return concatenatedStringValue;
	}
	
	private long nrOfTokensInState(WorkflowState state) {
		return state.tokens().asMap().values().stream().flatMap(Set::stream).count();
	}
}

class UnknownData implements ActionData{ }
