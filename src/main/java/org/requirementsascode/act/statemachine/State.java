package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.Behavior.identity;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.DoNothing;

public class State<S, V> implements Behavior<S, V> {
	private final String name;
	private final Predicate<S> invariant;
	private Behavior<S, V> behavior;

	private State(String name, Predicate<S> invariant, Behavior<S, V> behavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.invariant = requireNonNull(invariant, "invariant must be non-null!");

		requireNonNull(behavior, "behavior must be non-null!");
		this.behavior = createStateBehavior(behavior);
	}

	public static <S, V> State<S, V> state(String stateName, Predicate<S> stateInvariant) {
		return new State<>(stateName, stateInvariant, new DoNothing<>());
	}

	public static <S, V> State<S, V> state(String stateName, Predicate<S> stateInvariant, Behavior<S, V> stateBehavior) {
		return new State<>(stateName, stateInvariant, stateBehavior);
	}

	@Override
	public Data<S, V> actOn(Data<S, V> input) {
		Data<S, V> result = behavior.actOn(input);
		return result;
	}

	public String name() {
		return name;
	}

	public Predicate<S> invariant() {
		return invariant;
	}
	
	private Behavior<S, V> createStateBehavior(Behavior<S, V> stateBehavior) {
		return inCase(this::matchesStateIn,
			stateBehavior.andThen(inCase(this::matchesStateIn, identity(), this::throwsIllegalStateException)));
	}

	public boolean matchesStateIn(Data<S, V> data) {
		return invariant().test(data.state());
	}

	private Data<S, V> throwsIllegalStateException(Data<S, V> output) {
		throw new IllegalStateException("After behavior of state " + name() + " invariant is false! -> output: " + output);
	}

	@Override
	public String toString() {
		return "State [name=" + name + "]";
	}
}
