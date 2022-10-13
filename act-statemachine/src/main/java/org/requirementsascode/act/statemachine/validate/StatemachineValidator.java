package org.requirementsascode.act.statemachine.validate;

import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class StatemachineValidator {
	public static <S, V0> void validate(Statemachine<S, V0> statemachine) {
		validateStates(statemachine, Transition::fromState, "The following fromStates are not in the state list:");
		validateStates(statemachine, Transition::toState, "The following toStates are not in the state list:");
	}

	private static <S, V0> void validateStates(Statemachine<S, V0> statemachine,
		Function<Transition<S, ?>, State<S, ?>> transitionStateAccess, String message) {
		
		List<State<S, V0>> expectedStates = statemachine.states().stream().toList();
		State<S, V0> definedState = statemachine.definedState();
		State<S, V0> defaultState = statemachine.defaultState();
		
		List<State<S, ?>> statesNotInList = transitionsAndFlowsOf(statemachine)
			.map(transitionStateAccess)
			.filter(s -> !definedState.equals(s) && 
				!defaultState.equals(s) && !anyState().equals(s) && 
				!expectedStates.contains(s))
			.collect(Collectors.toList());

		if (!statesNotInList.isEmpty()) {
			throw new IllegalArgumentException(message + " " + statesNotInList);
		}
	}

	private static <V0, S> Stream<Transition<S, V0>> transitionsAndFlowsOf(Statemachine<S, V0> stateMachine) {
		Stream<Transition<S, V0>> transitions = 
			Stream.concat(
				stateMachine.flows().asTransitions(stateMachine).stream(),
				stateMachine.transitions().stream()
			);
		
		return transitions;
	}
}
