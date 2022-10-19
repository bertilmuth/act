package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class DefaultNode implements Node {
		private final State<Workflow, Token> defaultState;

		private DefaultNode(Statemachine<Workflow, Token> owningStatemachine) {
			requireNonNull(owningStatemachine, "owningStatemachine must be non-null!");
			this.defaultState = owningStatemachine.defaultState();
		}
		
		public static DefaultNode defaultNode(Statemachine<Workflow, Token> owningStatemachine) {
			return new DefaultNode(owningStatemachine);
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