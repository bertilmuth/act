package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InitialActions{
	private final List<InitialAction> initialActions;

	private InitialActions(List<Action> actions) {
		requireNonNull(actions, "initialActions must be non-null!");
		this.initialActions = createInitialActions(actions);
	}

	static InitialActions initialActions(List<Action> actions) {
		return new InitialActions(actions);
	}

	public Stream<InitialAction> stream() {
		return initialActions.stream();
	}
	
	private List<InitialAction> createInitialActions(List<Action> actions) {
		return actions.stream()
			.map(a -> InitialAction.initialAction(a))
			.collect(Collectors.toList());
	}
}
