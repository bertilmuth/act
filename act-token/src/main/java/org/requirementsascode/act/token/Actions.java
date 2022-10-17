package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Actions{
	private final List<Action> actions;

	private Actions(List<Action> actions) {
		this.actions = requireNonNull(actions, "actions must be non-null!");
	}

	static <S, V0> Actions actions(List<Action> actions) {
		return new Actions(actions);
	}

	public List<State<Workflow, Token>> asStates() {
		List<State<Workflow, Token>> statesList = this.stream()
				.map(e -> e.asState())
				.collect(Collectors.toList());
		return statesList;
	}

	public Stream<Action> stream() {
		return actions.stream();
	}
}
