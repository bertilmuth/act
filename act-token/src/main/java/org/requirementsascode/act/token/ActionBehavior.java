package org.requirementsascode.act.token;

import org.requirementsascode.act.core.Behavior;

public interface ActionBehavior {
	Behavior<WorkflowState, Token, Token> asBehavior(Action owningAction);
}
