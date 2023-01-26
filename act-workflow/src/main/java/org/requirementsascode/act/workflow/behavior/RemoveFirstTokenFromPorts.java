package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class RemoveFirstTokenFromPorts implements Behavior<WorkflowState, Token, Token> {
	private final Ports ports;
	
	public RemoveFirstTokenFromPorts(Ports ports) {
		this.ports = requireNonNull(ports, "ports must be non-null!");
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		return ports.stream()
	        .reduce(inputData, 
	        	(d, port) -> port.removeFirstToken(d), 
	        	(d1, d2) -> d2);
	}
}
