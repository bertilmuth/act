package org.requirementsascode.act.workflow.function;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

class AddTokenToOutPorts implements Behavior<WorkflowState, Token, Part> {
	private final Part owner;
	
	public AddTokenToOutPorts(Part owner) {
		this.owner = requireNonNull(owner, "owner must be non-null!");
	}

	@Override
	public Data<WorkflowState, Part> actOn(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, Token> result = owner.outPorts().stream()
	        .reduce(inputData, 
	        	(d, port) -> port.addToken(d.state(), Token.from(d)), 
	        	(d1, d2) -> d2);
		return data(result.state(), owner);
	}
}
