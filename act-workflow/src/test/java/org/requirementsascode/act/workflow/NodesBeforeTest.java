package org.requirementsascode.act.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.action;
import static org.requirementsascode.act.workflow.WorkflowApi.dataFlow;

import java.util.List;
import java.util.function.Predicate;

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
		Node action1 = action(ACTION1, StringData.class, this::action1Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		List<Node> nodesBefore = state.nodesBefore(workflow, action1);
		assertEquals(1, nodesBefore.size());
		assertTrue(nodesBefore.get(0) instanceof InitialNode);
	}
	
	@Test
	void action1Before() {
		Node action1 = action(ACTION1, StringData.class, this::action1Performed);
		Node action2 = action(ACTION2, StringData.class, this::action1Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2)
			)
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		List<Node> nodesBefore = state.nodesBefore(workflow, action2);
		assertEquals(1, nodesBefore.size());
		assertEquals(action1, nodesBefore.get(0));
	}
	
	@Test
	void noTokensInNodesBeforeAction1() {
		Node action1 = action(ACTION1, StringData.class, this::action1Performed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1)
			.startNodes(action1)
			.dataFlows()
			.build();
		
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		Predicate<WorkflowState> predicateBefore = state.areTokensInNodesBefore(action1);
		assertFalse(predicateBefore.test(state));
	}
	
	@Test
	void predicateBeforeAction2IsTrue() {
		Node action1 = action(ACTION1, StringData.class, this::action1Performed);
		Node action2i = action(ACTION2I, IntegerData.class, this::action2iPerformed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2i)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action1, action2i)
			)
			.build();
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		Predicate<WorkflowState> predicateBefore = state.areTokensInNodesBefore(action2i);
		assertTrue(predicateBefore.test(state));
	}
	
	@Test
	void predicateBeforeAction2IsFalse() {
		Node action1 = action(ACTION1, StringData.class, this::action1Performed);
		Node action2 = action(ACTION2, StringData.class, this::action1Performed);
		Node action2i = action(ACTION2I, IntegerData.class, this::action2iPerformed);
		
		Workflow workflow = Workflow.builder()
			.nodes(action1, action2, action2i)
			.startNodes(action1)
			.dataFlows(
				dataFlow(action2, action2i),
				dataFlow(action1, action2i)
			)
			.build();
		WorkflowState state = workflow.start(str(START_WORKFLOW)).state();
		Predicate<WorkflowState> predicateBefore = state.areTokensInNodesBefore(action2i);
		assertFalse(predicateBefore.test(state));
	}

	private StringData action1Performed(WorkflowState state, StringData input) {
		return new StringData("ActionPerformed");
	}
	
	private IntegerData action2iPerformed(WorkflowState state, IntegerData input) {
		return new IntegerData(input.integer + 1);
	}
	
	private StringData str(String str) {
		return new StringData(str);
	}
}
