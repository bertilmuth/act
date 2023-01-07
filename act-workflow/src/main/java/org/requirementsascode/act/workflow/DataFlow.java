package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class DataFlow<T extends ActionData> implements Transitionable<WorkflowState, Token>{
	private final Node fromNode;
	private final Node toNode;
	private final Predicate<T> guardCondition;

	DataFlow(Node fromNode, Node toNode, Predicate<T> guardCondition) {
		this.fromNode = requireNonNull(fromNode, "fromNode must be non-null!");
		this.toNode = requireNonNull(toNode, "toNode must be non-null!");
		this.guardCondition = requireNonNull(guardCondition, "guardCondition must be non-null!");
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(fromNode.asState(), toNode.asState(), this::moveToken);
	}
	
	@SuppressWarnings("unchecked")
	private Data<WorkflowState, Token> moveToken(Data<WorkflowState, Token> inputData) {
		Token inputToken = Token.from(inputData);
		WorkflowState state = inputData.state();

		return inputToken.actionData()
			.filter(ad -> guardCondition.test((T) ad))
			.map(ad -> moveToken(state, inputToken))
			.orElse(clearToken(state));
	}

	private Data<WorkflowState, Token> clearToken(WorkflowState state) {
		return data(state, null);
	}
	
	private Data<WorkflowState,Token> moveToken(WorkflowState state, Token tokenToMove){
		return state.moveToken(tokenToMove, fromNode, toNode);
	}
}
