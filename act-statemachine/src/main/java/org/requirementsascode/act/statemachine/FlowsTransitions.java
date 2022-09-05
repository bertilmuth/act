package org.requirementsascode.act.statemachine;

import java.util.List;
import java.util.stream.Collectors;

public class FlowsTransitions {
	public static <S, V0> List<Transition<S, V0>> flowsTransitions(Flows<S, V0> flows, State<S, V0> definedState,
			State<S, V0> defaultState) {
		return flows.asCollection().stream().map(e -> e.asTransition(definedState, defaultState))
				.collect(Collectors.toList());
	}
}
