package org.requirementsascode.act.workflow;

import org.requirementsascode.act.core.Behavior;

public interface ActionBehavior {
	Behavior<WorkflowState, Token, Token> asBehavior(Action owningAction);
}
