package org.requirementsascode.act.statemachine;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

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
			this.builderStates = new States<>(asList(states));
		}

		@SafeVarargs
		public final TransitionsBuilder transitions(Transitionable<S, V>... transitionsArray) {
			requireNonNull(transitionsArray, "transitionsArray must be non-null!");
			return new TransitionsBuilder(transitionsArray);
		}

		public class TransitionsBuilder {
			private TransitionsBuilder(Transitionable<S, V>[] transitions){
				requireNonNull(transitions, "transitions must be non-null!");
				StatesBuilder.this.builderTransitions = new Transitions<>(asList(transitions));
			}
			
			public Statemachine<S,V> build() {
				return new RecursivenessBuilder(false).build();
			}

			public RecursivenessBuilder isRecursive(boolean recursive) {
				return new RecursivenessBuilder(recursive);
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