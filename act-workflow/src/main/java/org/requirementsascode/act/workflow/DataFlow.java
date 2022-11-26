package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.workflow.trigger.StoreToken;

public class DataFlow implements Transitionable<WorkflowState, Token>{
	private final Node fromNode;
	private final Node toNode;

	DataFlow(Node fromNode, Node toNode) {
		this.fromNode = requireNonNull(fromNode, "fromNode must be non-null!");
		this.toNode = requireNonNull(toNode, "toNode must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(fromNode.asState(), toNode.asState(), this::moveToken);
	}
	
	private Data<WorkflowState,Token> moveToken(Data<WorkflowState,Token> inputDataWithToken){
		ActionData inputActionData = ActionData.from(inputDataWithToken);
		Token storeToken = Token.from(inputDataWithToken)
			.map(t -> t.replaceActionData(new StoreToken(t)))
			.orElse(null);

		return inputDataWithToken.state().moveToken(inputDataWithToken, toNode);
	}
}
