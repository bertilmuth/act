package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.List;

public class Transitions<S, V0> {
	private final List<Transition<S, V0>> transitions;

	private Transitions(List<Transition<S, V0>> transitions) {
		this.transitions = requireNonNull(transitions, "transitions must be non-null!");
	}

	public static <S, V0> Transitions<S, V0> transitions(List<Transition<S, V0>> transitions) {
		return new Transitions<>(transitions);
	}
	
	public List<Transition<S, V0>> asList() {
		return transitions;
	}
}
