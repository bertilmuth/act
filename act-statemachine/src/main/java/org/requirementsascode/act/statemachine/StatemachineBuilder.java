package org.requirementsascode.act.statemachine;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatemachineBuilder {
	StatemachineBuilder() {
	}

	@SafeVarargs
	public final <S,V> StatesBuilder<S,V> states(State<S,V>... states) {
		return new StatesBuilder<>(states);
	}

	public class StatesBuilder<S,V> {
		private final List<State<S,V>> builderStates;
		private Transitions<S, V> builderTransitions = Transitions.transitions(Collections.emptyList());
		private Flows<S, V> builderFlows = Flows.flows(Collections.emptyList());

		private StatesBuilder(State<S,V>[] states) {
			requireNonNull(states, "states must be non-null!");
			this.builderStates = Arrays.asList(states);
		}

		@SafeVarargs
		public final TransitionsBuilder transitions(Transition<S, V>... transitionsArray) {
			requireNonNull(transitionsArray, "transitionsArray must be non-null!");
			builderTransitions = Transitions.transitions(Arrays.asList(transitionsArray));
			return new TransitionsBuilder();
		}

		public class TransitionsBuilder {
			private TransitionsBuilder(){}
			
			@SafeVarargs
			public final FlowsBuilder flows(Flow<S, V>... flowsArray) {
				requireNonNull(flowsArray, "flowsArray must be non-null!");
				builderFlows = Flows.flows(asList(flowsArray));
				return new FlowsBuilder();
			}
			
			public final Statemachine<S,V> build() {
				return new FlowsBuilder().build();
			}
		}
		
		public class FlowsBuilder {
			private FlowsBuilder(){}
			
			public final Statemachine<S,V> build() {
				return new Statemachine<>(builderStates, builderTransitions, builderFlows);
			}
		}
	}
}