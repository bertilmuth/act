package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Actions {
	private final List<ExecutableNode> actions;

	Actions(List<ExecutableNode> actions) {
		this.actions = requireNonNull(actions, "actions must be non-null!");
	}

	Stream<State<WorkflowState, Token>> asStates() {
		Stream<State<WorkflowState, Token>> statesStream = this.stream().map(e -> e.asState());
		return statesStream;
	}

	public Stream<ExecutableNode> stream() {
		return actions.stream();
	}
}
