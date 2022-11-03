package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InitialActions{
	private final List<InitialAction> initialActions;

	InitialActions(List<Action> actions) {
		requireNonNull(actions, "actions must be non-null!");
		this.initialActions = createInitialActions(actions);
	}

	public Stream<InitialAction> stream() {
		return initialActions.stream();
	}
	
	private List<InitialAction> createInitialActions(List<Action> actions) {
		return actions.stream()
			.map(InitialAction::new)
			.collect(Collectors.toList());
	}
}
