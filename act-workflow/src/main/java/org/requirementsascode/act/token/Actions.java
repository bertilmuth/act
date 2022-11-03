package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Actions {
	private final List<Action> actions;

	Actions(List<Action> actions) {
		this.actions = requireNonNull(actions, "actions must be non-null!");
	}

	Stream<State<WorkflowState, Token>> asStates() {
		Stream<State<WorkflowState, Token>> statesStream = this.stream().map(e -> e.asState());
		return statesStream;
	}

	public Stream<Action> stream() {
		return actions.stream();
	}
}
