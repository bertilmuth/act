package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
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

	Stream<State<Workflow, Token>> asStates() {
		Stream<State<Workflow, Token>> statesStream = this.stream()
				.map(e -> e.asState());
		return statesStream;
	}

	public Stream<Action> stream() {
		return actions.stream();
	}
}
