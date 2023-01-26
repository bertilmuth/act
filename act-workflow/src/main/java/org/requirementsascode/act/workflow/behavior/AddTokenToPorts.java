package org.requirementsascode.act.workflow.behavior;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.workflow.Port;
import org.requirementsascode.act.workflow.Ports;
import org.requirementsascode.act.workflow.Token;
import org.requirementsascode.act.workflow.WorkflowState;

public class AddTokenToPorts implements Behavior<WorkflowState, Token, Token> {
	private final Ports ports;
	
	public AddTokenToPorts(Ports ports) {
		this.ports = requireNonNull(ports, "ports must be non-null!");
	}

	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		Port<?> port = ports.stream().findFirst().get();
		return port.addToken(inputData);
	}
}
