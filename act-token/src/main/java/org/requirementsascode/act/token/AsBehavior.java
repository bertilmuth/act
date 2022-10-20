package org.requirementsascode.act.token;

import org.requirementsascode.act.core.Behavior;

interface AsBehavior {
	Behavior<Workflow, Token, Token> asBehavior(Action callingAction);
}
