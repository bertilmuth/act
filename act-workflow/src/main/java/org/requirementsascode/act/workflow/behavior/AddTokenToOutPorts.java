package org.requirementsascode.act.workflow.behavior;

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
		assert(inputData.value().isPresent());
		Token token = inputData.value().get();
		
		WorkflowState resultState = owner.outPorts().stream()
	        .reduce(inputData.state(), 
	        	(d, port) -> port.addToken(d, token), 
	        	(d1, d2) -> d2);
		return data(resultState, owner);
	}
}
