package org.requirementsascode.act.statemachine.validate;

import static org.requirementsascode.act.statemachine.unitedbehavior.FlowsTransitions.flowsTransitions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class StatemachineValidator {
	public static <S, V0> void validate(Statemachine<S, V0> statemachine) {

		State<S, V0> definedState = statemachine.getDefinedState();
		State<S, V0> defaultState = statemachine.getDefaultState();
		List<Transition<S, ? extends V0, V0>> flowsTransitions = flowsTransitions(statemachine.getFlows(), definedState,
			defaultState);

		List<Transition<S, ? extends V0, V0>> transitions = new ArrayList<>(flowsTransitions);
		transitions.addAll(statemachine.getTransitions());

		List<State<S, V0>> expectedStates = statemachine.getStates();

		validateStates(transitions, expectedStates, definedState, defaultState, Transition::getFromState,
			"The following fromStates are not in the state list:");
		validateStates(transitions, expectedStates, definedState, defaultState, Transition::getToState,
			"The following toStates are not in the state list:");
	}

	private static <S, V0> void validateStates(List<Transition<S, ? extends V0, V0>> transitions,
		List<State<S, V0>> expectedStates, State<S, V0> definedState, State<S, V0> defaultState,
		Function<Transition<S, ?, ?>, State<S, ?>> transitionStateAccess, String message) {

		List<State<S, ?>> statesNotInList = transitions.stream()
			.map(transitionStateAccess)
			.filter(s -> !definedState.equals(s) && !defaultState.equals(s) && !expectedStates.contains(s))
			.collect(Collectors.toList());

		if (!statesNotInList.isEmpty()) {
			throw new IllegalArgumentException(message + " " + statesNotInList);
		}
	}
}
