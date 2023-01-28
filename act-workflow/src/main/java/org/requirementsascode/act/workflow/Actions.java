package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class Actions {
	private final List<Action> actions;

	Actions(List<Action> actions) {
		this.actions = requireNonNull(actions, "actions must be non-null!");
	}

	public Stream<Action> stream() {
		return actions.stream();
	}
	
	Stream<Flow> asFlows() {
		return stream().map(Action::asFlow);
	}
}