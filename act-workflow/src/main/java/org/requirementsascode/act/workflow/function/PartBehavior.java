package org.requirementsascode.act.workflow.function;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public interface PartBehavior {
	Behavior<WorkflowState,Token,Token> asBehavior(Part owner);
}
