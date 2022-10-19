package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

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
		DefaultNode defaultNode = new DefaultNode(owningStatemachine);
		return TokenFlow.tokenFlow(defaultNode, initialAction).asTransition(owningStatemachine);
	}

	public static class DefaultNode implements Node {
		private final State<Workflow, Token> defaultState;

		private DefaultNode(Statemachine<Workflow, Token> owningStatemachine) {
			this.defaultState = owningStatemachine.defaultState();
		}

		@Override
		public String name() {
			return "InitialAction";
		}

		@Override
		public State<Workflow, Token> asState() {
			return defaultState;
		}
	}
}
