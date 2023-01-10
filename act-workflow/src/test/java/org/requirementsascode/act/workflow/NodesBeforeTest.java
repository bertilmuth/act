package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;
import static org.requirementsascode.act.workflow.WorkflowApi.port;

import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.IntegerData;
import org.requirementsascode.act.workflow.testdata.StringData;

class NodesBeforeTest {
	private static final String PORT1 = "Port1";
	private static final String PORT2 = "Port2";
	private static final String PORT2I = "Port2i";
	
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	private static final String ACTION2I = "Action2i";
	
	private static final String START_WORKFLOW = "";

	@Test
	void initialNodeBefore() {
		Port<StringData> port1 = port(PORT1, StringData.class);
		Node action1 = createAction1(port1);
		
		Workflow workflow = Workflow.builder()
			.nodes(port1, action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		List<Node> nodesBefore = state.nodesBefore(action1, workflow);
		assertEquals(1, nodesBefore.size());
		assertTrue(nodesBefore.get(0) instanceof InitialNode);
	}
	
	@Test
	void action1Before() {
		Port<StringData> port1 = port(PORT1, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<StringData> port2 = port(PORT2, StringData.class);
		Node action2 = createAction2(port2);
		
		Workflow workflow = Workflow.builder()
			.nodes(port1, port2, action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		List<Node> nodesBefore = state.nodesBefore(action2, workflow);
		assertEquals(1, nodesBefore.size());
		assertEquals(action1, nodesBefore.get(0));
	}
	
	@Test
	void noTokensBeforeAction2() {
		Port<StringData> port1 = port(PORT1, StringData.class);
		Node action1 = createAction1(port1);
		
		Port<StringData> port2 = port(PORT2, StringData.class);
		Node action2 = createAction2(port2);
		
		Port<IntegerData> port2i = port(PORT2I, IntegerData.class);
		Node action2i = createAction2i(port2i);
		
		Workflow workflow = Workflow.builder()
			.nodes(port1, port2, port2i, action1, action2, action2i)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action2, action2i),
				dataFlow(action1, action2i)
			)
			.build();
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		boolean areTokensInNodeBefore = state.areTokensInNodesBefore(action2i);
		assertFalse(areTokensInNodeBefore);
	}
	private Node createAction1(Port<StringData> port1) {
		return createAction(port1, ACTION1, this::action1Performed);
	}
	
	private Node createAction2(Port<StringData> port2) {
		return createAction(port2, ACTION2, this::action2Performed);
	}
	
	private Node createAction2i(Port<IntegerData> port2i) {
		return createAction(port2i, ACTION2I, this::action2iPerformed);
	}
	
	private <T extends ActionData, U extends ActionData> Node createAction(Port<T> port, String actionName, BiFunction<WorkflowState, T, U> actionFunction) {
		return action(port, actionName, actionFunction);
	}

	private StringData action1Performed(WorkflowState state, StringData input) {
		return new StringData("ActionPerformed");
	}
	
	private StringData action2Performed(WorkflowState state, StringData input) {
		return new StringData("ActionPerformed");
	}
	
	private IntegerData action2iPerformed(WorkflowState state, IntegerData input) {
		return new IntegerData(input.integer + 1);
	}
	
	private StringData str(String str) {
		return new StringData(str);
	}
}
