package org.requirementsascode.act.statemachine.validate;

import static org.requirementsascode.act.statemachine.unitedbehavior.FlowsTransitions.flowsTransitions;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class StatemachineValidator {
	public static <S, V0> void validate(Statemachine<S, V0> statemachine) {

		State<S, V0> definedState = statemachine.definedState();
		State<S, V0> defaultState = statemachine.defaultState();
		List<Transition<S, V0>> flowsTransitions = flowsTransitions(statemachine.flows(), definedState,
			defaultState);

		List<Transition<S, V0>> transitions = new ArrayList<>(flowsTransitions);
		transitions.addAll(statemachine.transitions());

		List<State<S, V0>> expectedStates = statemachine.states();

		validateStates(transitions, expectedStates, definedState, defaultState, Transition::fromState,
			"The following fromStates are not in the state list:");
		validateStates(transitions, expectedStates, definedState, defaultState, Transition::toState,
			"The following toStates are not in the state list:");
	}

	private static <S, V0> void validateStates(List<Transition<S, V0>> transitions,
		List<State<S, V0>> expectedStates, State<S, V0> definedState, State<S, V0> defaultState,
		Function<Transition<S, ?>, State<S, ?>> transitionStateAccess, String message) {

		List<State<S, ?>> statesNotInList = transitions.stream()
			.map(transitionStateAccess)
			.filter(s -> !definedState.equals(s) && 
				!defaultState.equals(s) && !anyState().equals(s) && 
				!expectedStates.contains(s))
			.collect(Collectors.toList());

		if (!statesNotInList.isEmpty()) {
			throw new IllegalArgumentException(message + " " + statesNotInList);
		}
	}
}
