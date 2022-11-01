package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.token.DefaultNode.defaultNode;
import static org.requirementsascode.act.token.TokenFlow.tokenFlow;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
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
		return tokenFlow(defaultNode(owningStatemachine), initialAction)
			.asTransition(owningStatemachine);
	}
}

class DefaultNode implements Node {
	private final State<Workflow, Token> defaultState;

	private DefaultNode(Statemachine<Workflow, Token> statemachine) {
		requireNonNull(statemachine, "statemachine must be non-null!");
		this.defaultState = statemachine.defaultState();
	}

	public static DefaultNode defaultNode(Statemachine<Workflow, Token> statemachine) {
		return new DefaultNode(statemachine);
	}

	@Override
	public String name() {
		return defaultState.name();
	}

	@Override
	public State<Workflow, Token> asState() {
		return defaultState;
	}
}
