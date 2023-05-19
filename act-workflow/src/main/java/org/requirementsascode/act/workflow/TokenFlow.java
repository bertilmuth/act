package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.workflow.behavior.PartBehavior;

public class TokenFlow extends Flow<WorkflowState, Token>{
	private final Part owner;
	
	TokenFlow(Part owner, PartBehavior partBehavior) {
		super(
			owner.inPorts().asOneState(), 
			owner.outPorts().asOneState(), 
			partBehavior.asBehavior(owner)
		);
		this.owner = requireNonNull(owner, "owner must be non-null!");
	}

	public Ports inPorts() {
		return owner.inPorts();
	}

	public Ports outPorts() {
		return owner.outPorts();
	}
}
