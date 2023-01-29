package org.requirementsascode.act.workflow.function;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Part;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.WorkflowState;


class RemoveFirstTokenFromInPorts implements Behavior<WorkflowState, Part, Part> {	
	public RemoveFirstTokenFromInPorts() {
	}

	@Override
	public Data<WorkflowState, Part> actOn(Data<WorkflowState, Part> inputData) {
		Part owner = inputData.value().get();
		Ports ports = owner.inPorts();
		
		WorkflowState newState = ports.stream()
	        .reduce(inputData.state(), 
	        	(s, port) -> port.removeFirstToken(s), 
	        	(s1, s2) -> s2);
		return data(newState, owner);
	}
}
