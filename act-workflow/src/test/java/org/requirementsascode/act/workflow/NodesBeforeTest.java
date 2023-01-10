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
	private static final String START_WORKFLOW = "";
	private static final String ACTION1 = "Action1";
	private static final String ACTION2 = "Action2";
	private static final String ACTION2I = "Action2i";

	@Test
	void initialNodeBefore() {
		Node action1 = createAction1();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
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
		Node action1 = createAction1();
		Node action2 = createAction2();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
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
		Node action1 = createAction1();
		Node action2 = createAction2();
		Node action2i = createAction2i();
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2, action2i)
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
	
	private Node createAction1() {
		return createAction(ACTION1, StringData.class, this::action1Performed);
	}
	
	private Node createAction2() {
		return createAction(ACTION2, StringData.class, this::action2Performed);
	}
	
	private Node createAction2i() {
		return createAction(ACTION2I, IntegerData.class, this::action2iPerformed);
	}
	
	private <T extends ActionData, U extends ActionData> Node createAction(String inputPortName, Class<T> inputPortType, BiFunction<WorkflowState, T, U> actionFunction) {
		return action(port(inputPortName,inputPortType), actionFunction);
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
