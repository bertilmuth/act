package org.requirementsascode.act.token;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

class DefaultNode implements Node {
		private final State<Workflow, Token> defaultState;

		DefaultNode(Statemachine<Workflow, Token> owningStatemachine) {
			this.defaultState = owningStatemachine.defaultState();
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