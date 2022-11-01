package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class InitialAction implements Flow<Workflow, Token> {
	private final Action initialAction;

	InitialAction(Action initialAction) {
		this.initialAction = requireNonNull(initialAction, "initialAction must be non-null!");
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return tokenFlow(new DefaultNode(owningStatemachine), initialAction)
			.asTransition(owningStatemachine);
	}
}
