package org.requirementsascode.act.workflow;

import org.requirementsascode.act.statemachine.Transitionable;

public interface Part extends Transitionable<WorkflowState, Token>{
	Ports inPorts();
	Ports outPorts();
}
