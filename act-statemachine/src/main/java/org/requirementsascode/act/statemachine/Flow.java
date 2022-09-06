package org.requirementsascode.act.statemachine;

public interface Flow<S, V0>{
	Transition<S, V0> asTransition(Statemachine<S,V0> owningStatemachine);
}
