package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class Actions {
	private final List<ActionNode<?,?>> actions;

	Actions(List<ActionNode<?,?>> actions) {
		this.actions = requireNonNull(actions, "actions must be non-null!");
	}

	public Stream<ActionNode<?,?>> stream() {
		return actions.stream();
	}
}