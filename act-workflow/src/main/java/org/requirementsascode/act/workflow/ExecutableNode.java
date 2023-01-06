package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class ExecutableNode implements Node {
	private final String name;
	private final Behavior<WorkflowState, ActionData, ActionData> actionBehavior;

	ExecutableNode(String name, Behavior<WorkflowState, ActionData, ActionData> actionBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
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
		return actOn(inputData);
	}

	@Override
	public String toString() {
		return "ExecutableNode[" + name + "]";
	}
	
	private Data<WorkflowState, Token> actOn(Data<WorkflowState, Token> inputData) {
		Data<WorkflowState, ActionData> functionInputData = unboxActionData(inputData);
		ActionData outputActionData = executeBehavior(functionInputData).value().orElse(null);

		Token inputToken = Token.from(inputData);
		Token outputToken = inputToken.replaceActionData(outputActionData);

		Data<WorkflowState, Token> updatedWorkflow = inputData.state().replaceToken(inputToken, outputToken);
		return updatedWorkflow;
	}
	
	private Data<WorkflowState, ActionData> unboxActionData(Data<WorkflowState, Token> inputData) {
		return data(inputData.state(), ActionData.from(inputData));
	}
	
	private Data<WorkflowState, ActionData> executeBehavior(Data<WorkflowState, ActionData> functionInputData) {
		return actionBehavior.actOn(functionInputData);
	}
}
