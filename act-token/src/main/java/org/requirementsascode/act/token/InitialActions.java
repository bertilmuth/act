package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class InitialActions{
	private final List<InitialAction> initialActions;

	private InitialActions(List<InitialAction> initialActions) {
		this.initialActions = requireNonNull(initialActions, "initialActions must be non-null!");
	}

	static InitialActions initialActions(List<InitialAction> initialActions) {
		return new InitialActions(initialActions);
	}

	public Stream<InitialAction> stream() {
		return initialActions.stream();
	}
}
