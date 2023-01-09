package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class DataFlow<T extends ActionData> implements Transitionable<WorkflowState, Token>{
	private final Node fromNode;
	private final Node toNode;
	private final Class<T> inputClass;
	private final Predicate<T> guardCondition;

	DataFlow(Node fromNode, Node toNode, Class<T> inputClass, Predicate<T> guardCondition) {
		this.fromNode = requireNonNull(fromNode, "fromNode must be non-null!");
		this.toNode = requireNonNull(toNode, "toNode must be non-null!");
		this.inputClass = inputClass;
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

		Data<WorkflowState, Token> outputData = inputToken.actionData()
			.filter(ad -> inputClass.isAssignableFrom(ad.getClass()))
			.filter(ad -> guardCondition.test((T) ad))
			.map(ad -> {
				WorkflowState stateWithTokensBeforeRemoved = removeTokensInNodesBefore(fromNode, state);
				WorkflowState stateWithTokenAdded = stateWithTokensBeforeRemoved.addToken(fromNode, inputToken).state();
				return data(stateWithTokenAdded, inputToken);
			})
			.orElse(clearToken(state));
		
		return outputData;
	}

	private Data<WorkflowState, Token> clearToken(WorkflowState state) {
		return data(state, null);
	}
	
	private WorkflowState removeTokensInNodesBefore(Node node, WorkflowState state) {
		List<Node> nodesBefore = state.nodesBefore(node, state.workflow());
		for (Node nodeBefore : nodesBefore) {
			Optional<Token> firstTokenInNode = state.firstTokenIn(nodeBefore);
			if(firstTokenInNode.isPresent()) {
				state = state.removeToken(nodeBefore, firstTokenInNode.get()).state();
			}
		}
		return state;
	}
}
