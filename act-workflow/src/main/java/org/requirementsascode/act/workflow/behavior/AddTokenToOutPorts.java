package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

class AddTokenToOutPorts implements Behavior<WorkflowState, Token, Token> {
	private final Part owner;
	
	public AddTokenToOutPorts(Part owner) {
		this.owner = requireNonNull(owner, "owner must be non-null!");
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		Token token = Token.from(inputData);
		
		WorkflowState resultState = owner.outPorts().stream()
	        .reduce(inputData.state(), 
	        	(d, port) -> port.addToken(d, token), 
	        	(d1, d2) -> d2);
		return data(resultState, token);
	}
}
