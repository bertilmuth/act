package org.requirementsascode.act.statemachine;

interface AsTransition<S,V0> {
	Transition<S, V0> asTransition(Statemachine<S,V0> owningStatemachine);
}
