package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class DataFlow implements Flow<WorkflowState, Token>{
	private final Node fromNode;
	private final Node toNode;

	private DataFlow(Node fromNode, Node toNode) {
		this.fromNode = requireNonNull(fromNode, "fromNode must be non-null!");
		this.toNode = requireNonNull(toNode, "toNode must be non-null!");
	}
	
	public static DataFlow dataFlow(Node fromNode, Node toNode) {		
		return new DataFlow(fromNode, toNode);
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(fromNode.asState(), toNode.asState(), d -> d.state().moveToken(d, toNode));
	}
}
