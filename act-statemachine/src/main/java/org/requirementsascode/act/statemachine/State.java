package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.Objects;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.NoOp;

public class State<S, V> implements Behavioral<S, V> {
	private final String name;
	private final Predicate<S> invariant;
	private Behavior<S, V, V> stateInternalBehavior;

	private State(String name, Predicate<S> invariant, Behavior<S, V, V> stateInternalBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.invariant = requireNonNull(invariant, "invariant must be non-null!");
		this.stateInternalBehavior = requireNonNull(stateInternalBehavior, "stateInternalBehavior must be non-null!");
	}

	static <S, V> State<S, V> state(String stateName, Predicate<S> stateInvariant) {
		return new State<>(stateName, stateInvariant, new NoOp<>());
	}

	static <S, V> State<S, V> state(String stateName, Predicate<S> stateInvariant, Behavior<S, V, V> stateInternalBehavior) {
		return new State<>(stateName, stateInvariant, stateInternalBehavior);
	}

	static <S, V> State<S, V> anyState() {
		return state("Any State", s -> true);
	}

	public String name() {
		return name;
	}

	public Predicate<S> invariant() {
		return invariant;
	}

	@Override
	public Behavior<S, V, V> asBehavior(Statemachine<S, V> sm) {
		return stateBehavior(sm).andThen(outgoingTransitionsBehavior(sm));
	}
	
	private Behavior<S, V, V> stateBehavior(Statemachine<S, V> sm) {
		return transition(this, this, stateInternalBehavior).asBehavior(sm);
	}

	private Behavior<S, V, V> outgoingTransitionsBehavior(Statemachine<S, V> sm) {
		return inCase(d -> d.value().isPresent(), 
			inCase(d -> sm.isTerminal(this), Behavior.identity(), transitionsBehavior(sm)));
	}

	private Behavior<S, V, V> transitionsBehavior(Statemachine<S, V> sm) {
		return sm.transitions().asBehavior(sm);
	}

	public boolean matchesStateIn(Data<S, ?> data) {
		return invariant().test(data.state());
	}

	@Override
	public String toString() {
		return "State [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State<?, ?> other = (State<?, ?>) obj;
		return Objects.equals(name, other.name);
	}
}
