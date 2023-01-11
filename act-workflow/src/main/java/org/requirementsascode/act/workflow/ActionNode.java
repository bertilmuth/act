package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class ActionNode<T extends ActionData, U extends ActionData> implements Node {
	private String actionName;
	private final Port<T> inputPort;
	private final Port<U> outputPort;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	ActionNode(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.actionName = requireNonNull(actionName, "actionName must be non-null!");
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
	}

	@Override
	public String name() {
		return actionName;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(actionName, this::areTokensInInputOrOutputPort,
			d -> {
				Statemachine<WorkflowState, Token> sm = d.state().statemachine();
				Transition<WorkflowState, Token> transition = new DataFlow<>(inputPort, outputPort, actionFunction).asTransition(sm);
				Data<WorkflowState, Token> result = transition.asBehavior(sm).actOn(d);
				return result;
			});
	}
	
	private boolean areTokensInInputOrOutputPort(WorkflowState state) {
		return state.areTokensIn(inputPort) || state.areTokensIn(outputPort);
	}

	@Override
	public String toString() {
		return "ActionNode[" + name() + "]";
	}
}
