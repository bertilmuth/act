package org.requirementsascode.act.statemachine.unitedbehavior;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Transition;

public class FlowsTransitions {
	public static <S, V0> List<Transition<S, ? extends V0, V0>> flowsTransitions(List<Flow<S, ? extends V0, V0>> flows,
		State<S, V0> definedState, State<S, V0> defaultState) {
		return flows.stream().map(e -> e.toTransition(definedState, defaultState)).collect(Collectors.toList());
	}
}
