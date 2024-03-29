package org.requirementsascode.act.statemachine;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.function.ConsumeWith;
import org.requirementsascode.act.statemachine.function.Init;
import org.requirementsascode.act.statemachine.function.MapWith;
import org.requirementsascode.act.statemachine.function.When;
import org.requirementsascode.act.statemachine.function.WhenInCase;

public class StatemachineApi {	
	public static <S, V> Data<S, V> data(S state, V value) {
		return Data.data(state, value);
	}
	
	public static <S, V> State<S, V> state(String stateName, Predicate<S> stateInvariant) {
		return new State<>(stateName, stateInvariant);
	}
	
	public static <S, V> State<S, V> state(String stateName, Predicate<S> stateInvariant, Behavior<S, V, V> stateBehavior) {
		return new State<>(stateName, stateInvariant, stateBehavior);
	}
	
	public static<S, V> State<S, V> anyState() {
		return State.anyState();
	}
	
	public static <S, V0> TriggeredTransition<S, V0> transition(State<S, V0> fromState, State<S, V0> toState,
			Behavior<S, V0, V0> transitionBehavior) {
		return new TriggeredTransition<>(fromState, toState, transitionBehavior);
	}

	public static <S, V0> SelfTransition<S, V0> selfTransition(State<S, V0> state, Behavior<S, V0, V0> transitionBehavior) {
		return new SelfTransition<>(state, transitionBehavior);
	}
	
	public static <S, V0> Flow<S, V0> flow(State<S, V0> fromState, State<S, V0> toState, Behavior<S, V0, V0> flowBehavior) {
		return new Flow<>(fromState, toState, flowBehavior);
	}
	
	public static <S, V1 extends V0, V2 extends V0, V0> Behavior<S,V0,V0> when(Class<V1> expectedType, Behavior<S, V1,V2> behavior) {
		return new When<>(expectedType, behavior);
	}
	
	public static <S, V1 extends V0, V2 extends V0, V0> Behavior<S,V0,V0> whenInCase(Class<V1> expectedType, Predicate<Data<S,V1>> predicate, Behavior<S, V1,V2> behavior) {
		return new WhenInCase<>(expectedType, predicate, behavior);
	}
	
	public static <S,V> Behavior<S,V,V> consumeWith(BiFunction<S,V,S> consumer){
		return new ConsumeWith<>(consumer);
	}
	
	public static <S,V1,V2> Behavior<S,V1,V2> mapWith(BiFunction<S,V1,Data<S,V2>> mapper){
		return new MapWith<>(mapper);
	}
	
	public static <S,V> Behavior<S,V,V> init(Function<V,S> init){
		return new Init<>(init);
	}
	
	public static <S, V0> EntryFlow<S, V0> entryFlow(Behavior<S, V0,V0> entryBehavior) {
		return entryFlow(null, entryBehavior);
	}
	
	public static <S, V0> EntryFlow<S, V0> entryFlow(State<S, V0> toState, Behavior<S, V0,V0> entryBehavior) {
		return new EntryFlow<>(toState, entryBehavior);
	}
	
	public static <S, V0> ExitFlow<S, V0> exitFlow(State<S, V0> fromState, Behavior<S, V0, V0> exitBehavior) {
		return new ExitFlow<>(fromState, exitBehavior);
	}
}
