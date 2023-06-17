package org.requirementsascode.act.statemachine.validate;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class StatemachineValidator {
	public static <S, V0> void validate(Statemachine<S, V0> statemachine) {
		requireNonNull(statemachine, "statemachine must be non-null!");
		validateStates(statemachine, Transition::fromState, "The following fromStates are not in the state list:");
		validateStates(statemachine, Transition::toState, "The following toStates are not in the state list:");
	}

	private static <S, V0> void validateStates(Statemachine<S, V0> statemachine,
		Function<Transition<S, ?>, State<S, ?>> transitionStateAccess, String message) {
		requireNonNull(statemachine, "statemachine must be non-null!");
		requireNonNull(transitionStateAccess, "transitionStateAccess must be non-null!");
		requireNonNull(message, "message must be non-null!");
		
		List<State<S, V0>> expectedStates = statemachine.states().stream().collect(Collectors.toList());
		State<S, V0> definedState = statemachine.definedState();
		State<S, V0> initialState = statemachine.defaultState();
		State<S, V0> finalState = statemachine.finalState();
		
		List<State<S, ?>> statesNotInList = transitionsOf(statemachine)
			.map(t -> t.asTransition(statemachine))
			.map(transitionStateAccess)
			.filter(s -> 
				!definedState.equals(s) && 
				!initialState.equals(s) && 
				!finalState.equals(s) && 
				!anyState().equals(s) && 
				!expectedStates.contains(s))
			.collect(Collectors.toList());

		if (!statesNotInList.isEmpty()) {
			throw new IllegalArgumentException(message + " " + statesNotInList);
		}
	}

	private static <V0, S> Stream<Transitionable<S, V0>> transitionsOf(Statemachine<S, V0> stateMachine) {
		return stateMachine.transitionables().stream();
	}
}
