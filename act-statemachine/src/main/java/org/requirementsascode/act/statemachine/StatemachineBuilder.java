package org.requirementsascode.act.statemachine;

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
		private List<Transition<S, V>> builderTransitions;
		private Flows<S, V> builderFlows = Flows.of(Collections.emptyList());

		private StatesBuilder(State<S,V>[] states) {
			requireNonNull(states, "states must be non-null!");
			this.builderStates = Arrays.asList(states);
		}

		@SafeVarargs
		public final TransitionsBuilder transitions(Transition<S, V>... transitions) {
			requireNonNull(transitions, "transitions must be non-null!");
			builderTransitions = Arrays.asList(transitions);
			return new TransitionsBuilder();
		}

		public class TransitionsBuilder {
			private TransitionsBuilder(){}
			
			@SafeVarargs
			public final FlowsBuilder flows(Flow<S, V>... flows) {
				requireNonNull(flows, "flows must be non-null!");
				builderFlows = Flows.of(Arrays.asList(flows));
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