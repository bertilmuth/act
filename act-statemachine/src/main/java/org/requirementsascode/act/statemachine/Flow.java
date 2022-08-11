package org.requirementsascode.act.statemachine;

public interface Flow<S,V0> {
	Transition<S, V0> convertToTransition(State<S,V0> definedState, State<S,V0> defaultState);
}
