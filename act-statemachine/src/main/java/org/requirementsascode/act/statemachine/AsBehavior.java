package org.requirementsascode.act.statemachine;

import org.requirementsascode.act.core.Behavior;

interface AsBehavior<S, V0> {
	Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine);
}
