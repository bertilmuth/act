package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class InitialAction implements Flow<Workflow, Token> {
	private final Action initialAction;

	private InitialAction(Action initialAction) {
		this.initialAction = requireNonNull(initialAction, "initialAction must be non-null!");
	}

	public static InitialAction initialAction(Action initialAction) {
		return new InitialAction(initialAction);
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		DefaultNode defaultNode = new DefaultNode(owningStatemachine);
		return TokenFlow.tokenFlow(defaultNode, initialAction).asTransition(owningStatemachine);
	}
}
