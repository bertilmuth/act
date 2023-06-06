package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;


class RemoveFirstTokenFromInPorts implements Behavior<WorkflowState, Token, Token> {	
	private final Part owner;

	public RemoveFirstTokenFromInPorts(Part owner) {
		this.owner = requireNonNull(owner, "owner must be non-null!");
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		Token token = Token.from(inputData);
		Ports ports = owner.inPorts();
		
		WorkflowState newState = ports.stream()
	        .reduce(inputData.state(), 
	        	(s, port) -> port.removeFirstToken(s), 
	        	(s1, s2) -> s2);
		return data(newState, token);
	}
}
