package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class ActionNode<T extends ActionData, U extends ActionData> implements Node {
	private String actionName;
	private final Port<T> inputPort;
	private final Port<U> outputPort;
	private final DataFlow<T,U> actionFlow;

	ActionNode(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.actionName = requireNonNull(actionName, "actionName must be non-null!");
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
		this.actionFlow = new DataFlow<>(inputPort, outputPort, actionFunction);
	}

	@Override
	public String name() {
		return actionName;
	}

	@Override
	public State<WorkflowState, Token> asState() {
		return state(actionName, this::areTokensInInputOrOutputPort, this::executeActionBehavior);
	}
	
	private Data<WorkflowState, Token> executeActionBehavior(Data<WorkflowState, Token> inputData){
		Statemachine<WorkflowState, Token> sm = inputData.state().statemachine();
		return actionBehavior(sm).actOn(inputData);
	}

	private Behavior<WorkflowState, Token, Token> actionBehavior(Statemachine<WorkflowState, Token> sm) {
		return actionFlow.asTransition(sm).asBehavior(sm);
	}
	
	private boolean areTokensInInputOrOutputPort(WorkflowState state) {
		return state.areTokensIn(inputPort) || state.areTokensIn(outputPort);
	}

	@Override
	public String toString() {
		return "ActionNode[" + name() + "]";
	}
}
