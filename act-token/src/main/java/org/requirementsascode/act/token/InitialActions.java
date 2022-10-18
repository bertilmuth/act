package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class InitialActions{
	private final List<Action> initialActions;

	private InitialActions(List<Action> initialActions) {
		this.initialActions = requireNonNull(initialActions, "initialActions must be non-null!");
	}

	static InitialActions initialActions(List<Action> initialActions) {
		return new InitialActions(initialActions);
	}

	public Stream<Action> stream() {
		return initialActions.stream();
	}
}
