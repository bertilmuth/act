package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.DoNothing;

public class State<S,V> implements Behavior<S,V>{
	private final String name;
	private final Predicate<S> invariant;
	private Behavior<S, V> behavior;
		
	private State(String name, Predicate<S> invariant, Behavior<S,V> behavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.invariant = requireNonNull(invariant, "invariant must be non-null!");
		
		requireNonNull(behavior, "behavior must be non-null!");
		this.behavior = createConditionalStateBehavior(behavior);
	}

	public static <S,V> State<S,V> state(String stateName, Predicate<S> stateInvariant){
		return new State<>(stateName, stateInvariant, new DoNothing<>());
	}
		
	public static <S,V> State<S,V> state(String stateName, Predicate<S> stateInvariant, Behavior<S,V> stateBehavior){		
		return new State<>(stateName, stateInvariant, stateBehavior);
	}
	
	@Override
	public Data<S, V> actOn(Data<S, V> input) {		
		Data<S, V> result = behavior.actOn(input);
		return result;
	}
	
	public String getName() {
		return name;
	}
	
	public Predicate<S> getInvariant() {
		return invariant;
	}
	
	public boolean matchesStateIn(Data<S,V> data) {
		return getInvariant().test(data.getState());
	}
	
	private Behavior<S, V> createConditionalStateBehavior(Behavior<S, V> stateBehavior) {
		return inCase(this::matchesStateIn, stateBehavior);
	}

	@Override
	public String toString() {
		return "State [name=" + name + "]";
	}
}
