package org.requirementsascode.act.statemachine;

import org.requirementsascode.act.core.Behavior;

public interface Flow<S, V0> extends AsBehavior<S, V0>{
	Transition<S, V0> asTransition(Statemachine<S,V0> owningStatemachine);
	default Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine){
		return asTransition(owningStatemachine).asBehavior(owningStatemachine);
	}
}
