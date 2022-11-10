package org.requirementsascode.act.statemachine;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;

import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;


public class StatemachineBuilder {
	StatemachineBuilder() {
	}
	
	@SafeVarargs
	public final <S,V> StatesBuilder<S,V> states(State<S,V>... states) {
		return new StatesBuilder<>(states, new OnlyOneBehaviorMayAct<>());
	}
	
	public <S,V> MergeStrategyBuilder<S,V> mergeStrategy(MergeStrategy<S,V> mergeStrategy) {
		return new MergeStrategyBuilder<>(mergeStrategy);
	}
	
	public class MergeStrategyBuilder<S,V>{
		private MergeStrategy<S, V> mergeStrategy;
		
		private MergeStrategyBuilder(MergeStrategy<S,V> mergeStrategy){
			this.mergeStrategy = mergeStrategy;
		}
		
		@SuppressWarnings("unchecked")
		public StatesBuilder<S,V> states(State<S,V>... states) {
			return new StatesBuilder<>(states, mergeStrategy);
		}
	}

	public class StatesBuilder<S,V> {
		private final MergeStrategy<S, V> mergeStrategy;
		private final States<S, V> builderStates;
		private Transitions<S, V> builderTransitions;
		private Transitions<S, V> builderFlows;

		private StatesBuilder(State<S,V>[] states, MergeStrategy<S, V> mergeStrategy) {
			requireNonNull(states, "states must be non-null!");
			this.mergeStrategy = requireNonNull(mergeStrategy, "mergeStrategy must be non-null!");
			this.builderStates = States.states(asList(states), mergeStrategy);
			this.builderTransitions = Transitions.transitions(Collections.emptyList());
			this.builderFlows = Transitions.transitions(Collections.emptyList());
		}

		@SafeVarargs
		public final TransitionsBuilder transitions(Transitionable<S, V>... transitionsArray) {
			requireNonNull(transitionsArray, "transitionsArray must be non-null!");
			builderTransitions = Transitions.transitions(asList(transitionsArray));
			return new TransitionsBuilder();
		}

		public class TransitionsBuilder {
			private TransitionsBuilder(){}
			
			public final Statemachine<S,V> build() {
				return new Statemachine<>(builderStates, builderTransitions, builderFlows, mergeStrategy);
			}
		}
	}
}