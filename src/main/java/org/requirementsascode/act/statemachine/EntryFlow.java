package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.Transition.transition;

import java.util.Optional;

import org.requirementsascode.act.core.Behavior;

public class EntryFlow<S, V extends V0, V0> implements Flow<S, V0, V0>{
	private final State<S, V0> toState;
	private final Behavior<S, V0> entryBehavior;

	private EntryFlow(State<S, V0> toState, Behavior<S, V0> entryBehavior) {
		this.toState = toState;
		this.entryBehavior = requireNonNull(entryBehavior, "entryBehavior must be non-null");
	}
	
	public static <S, V extends V0, V0> EntryFlow<S, V, V0> entryFlow(Behavior<S, V0> entryBehavior) {
		return new EntryFlow<>(null, entryBehavior);
	}

	public static <S, V extends V0, V0> EntryFlow<S, V, V0> entryFlow(State<S, V0> toState, Behavior<S, V0> entryBehavior) {
		requireNonNull(toState, "toState must be non-null");
		return new EntryFlow<>(toState, entryBehavior);
	}

	public Optional<State<S, V0>> toState() {
		return Optional.ofNullable(toState);
	}

	public Behavior<S, V0> entryBehavior() {
		return entryBehavior;
	}

	@Override
	public Transition<S, V0, V0> convertToTransition(State<S, V0> definedState, State<S, V0> defaultState) {
		State<S, V0> toStateOrAnyDefinedState = toState().orElse(definedState);
		return transition(defaultState, toStateOrAnyDefinedState, entryBehavior());
	}
}