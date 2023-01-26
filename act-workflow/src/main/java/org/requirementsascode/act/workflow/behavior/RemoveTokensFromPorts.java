package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

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
		WorkflowState outputState = ports.stream()
	        .reduce(inputData.state(), 
	        	(state, port) -> port.removeFirstToken(state), 
	        	(state1, state2) -> state2);
		return data(outputState, Token.from(inputData));
	}
}
