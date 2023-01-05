package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class ExecutableNode implements Node {
	private final String name;
	private final Behavior<WorkflowState, Token, Token> executableBehavior;

	ExecutableNode(String name, Behavior<WorkflowState, Token, Token> executableBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.executableBehavior = requireNonNull(executableBehavior, "executableBehavior must be non-null!");
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(name(), s -> s.areTokensIn(this), this::consumeToken);
	}

	private Data<WorkflowState, Token> consumeToken(Data<WorkflowState, Token> inputData) {
		return executableBehavior.actOn(inputData);
	}

	@Override
	public String toString() {
		return "ExecutableNode[" + name + "]";
	}
}
