package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class RemoveTokensFromPorts implements Behavior<WorkflowState, Token, Token> {
	private final Ports ports;
	
	public RemoveTokensFromPorts(Ports ports) {
		this.ports = requireNonNull(ports, "ports must be non-null!");
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> outputData = ports.stream()
	        .reduce(inputData, 
	        	(d, port) -> port.removeFirstToken(d), 
	        	(d1, d2) -> d2);
		return data(outputData.state(), Token.from(inputData));
	}
}
