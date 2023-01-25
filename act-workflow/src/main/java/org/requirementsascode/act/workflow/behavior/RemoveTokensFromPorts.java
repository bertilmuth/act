package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Port;
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
	        	(s, port) -> removeTokenFromPort(s, port), 
	        	(data1, data2) -> data2);
		return data(outputState, Token.from(inputData));
	}
	
	private WorkflowState removeTokenFromPort(WorkflowState state, Port<?> inputPort) {
		return state.firstTokenIn(inputPort)
			.map(token -> state.removeToken(inputPort, token))
			.map(Data::state)
			.orElse(state);
	}
}
