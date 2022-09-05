package org.requirementsascode.act.statemachine;

public interface AsTransition<S,V0> {
	Transition<S, V0> asTransition(State<S,V0> definedState, State<S,V0> defaultState);
}
