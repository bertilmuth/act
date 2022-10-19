package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class InitialAction implements Flow<Workflow, Token>{
	private final Action fromAction;

	private InitialAction(Action fromAction) {
		this.fromAction = requireNonNull(fromAction, "fromAction must be non-null!");
	}
	
	public static InitialAction initialAction(Action fromAction) {		
		return new InitialAction(fromAction);
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return TokenFlow.tokenFlow(fromAction, fromAction).asTransition(owningStatemachine);
	}
}
