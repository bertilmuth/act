package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatemachineBuilder {
	StatemachineBuilder() {
	}

	@SafeVarargs
	public final <S,V> States<S,V> states(State<S,V>... states) {
		return new States<>(states);
	}

	public class States<S,V> {
		private final List<State<S,V>> builderStates;
		private List<Transition<S, V>> builderTransitions;
		private List<AsTransition<S, V>> builderFlows = new ArrayList<>();

		private States(State<S,V>[] states) {
			requireNonNull(states, "states must be non-null!");
			this.builderStates = Arrays.asList(states);
		}

		@SafeVarargs
		public final Transitions transitions(Transition<S, V>... transitions) {
			requireNonNull(transitions, "transitions must be non-null!");
			builderTransitions = Arrays.asList(transitions);
			return new Transitions();
		}

		public class Transitions {
			private Transitions(){}
			
			@SafeVarargs
			public final Flows flows(AsTransition<S, V>... flows) {
				requireNonNull(flows, "flows must be non-null!");
				builderFlows = Arrays.asList(flows);
				return new Flows();
			}
			
			public final Statemachine<S,V> build() {
				return new Flows().build();
			}
		}
		
		public class Flows {
			private Flows(){}
			
			public final Statemachine<S,V> build() {
				return new Statemachine<>(builderStates, builderTransitions, builderFlows);
			}
		}
	}
}