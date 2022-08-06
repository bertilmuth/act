package org.requirementsascode.act.statemachine;

public interface Flow<S,V extends V0,V0> {
	Transition<S, V, V0> convertToTransition(State<S,V0> definedState, State<S,V0> defaultState);
}
