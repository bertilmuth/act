package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow implements Flow<Workflow, Token>{
	private final Node fromNode;
	private final Node toNode;

	private TokenFlow(Node fromNode, Node toNode) {
		this.fromNode = requireNonNull(fromNode, "fromNode must be non-null!");
		this.toNode = requireNonNull(toNode, "toNode must be non-null!");
	}
	
	public static TokenFlow tokenFlow(Node fromNode, Node toNode) {		
		return new TokenFlow(fromNode, toNode);
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(fromNode.asState(), toNode.asState(), d -> Workflow.from(d).moveToken(d, toNode));
	}
}
