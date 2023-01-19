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
		return new StatesBuilder<>(states);
	}

	public class StatesBuilder<S,V> {
		private final States<S, V> builderStates;
		private Transitions<S, V> builderTransitions;
		private boolean isRecursive = false;
		private MergeStrategy<S, V> mergeStrategy;

		private StatesBuilder(State<S,V>[] states) {
			requireNonNull(states, "states must be non-null!");
			this.builderStates = States.states(asList(states));
			this.builderTransitions = new Transitions<>(Collections.emptyList());
		}

		@SafeVarargs
		public final TransitionsBuilder transitions(Transitionable<S, V>... transitionsArray) {
			requireNonNull(transitionsArray, "transitionsArray must be non-null!");
			builderTransitions = new Transitions<>(asList(transitionsArray));
			return new TransitionsBuilder();
		}

		public class TransitionsBuilder {
			private TransitionsBuilder(){}
			
			public Statemachine<S,V> build() {
				return new RecursivenessBuilder(false).build();
			}

			public RecursivenessBuilder isRecursive(boolean isRecursive) {
				return new RecursivenessBuilder(isRecursive);
			}
		}
		
		public class RecursivenessBuilder{
			private RecursivenessBuilder(boolean isRecursive) {
				StatesBuilder.this.isRecursive = isRecursive;
			}
			
			public MergeStrategyBuilder mergeStrategy(MergeStrategy<S,V> mergeStrategy) {
				return new MergeStrategyBuilder(mergeStrategy);
			}
			
			public final Statemachine<S,V> build() {
				return new MergeStrategyBuilder(new OnlyOneBehaviorMayAct<>()).build();
			}
			
			public class MergeStrategyBuilder{				
				private MergeStrategyBuilder(MergeStrategy<S,V> mergeStrategy){
					StatesBuilder.this.mergeStrategy = mergeStrategy;
				}

				public Statemachine<S, V> build() {
					return new Statemachine<>(builderStates, builderTransitions, mergeStrategy, isRecursive);
				}
			}
		}
	}
}