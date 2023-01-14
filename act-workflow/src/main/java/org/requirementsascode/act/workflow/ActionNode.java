package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.workflow.WorkflowApi.flow;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;

public class ActionNode<T extends ActionData, U extends ActionData> implements Node, Transitionable<WorkflowState, Token> {
	private String actionName;
	private final Port<T> inputPort;
	private final Port<U> outputPort;
	private final Flow<T,U> actionFlow;

	ActionNode(String actionName, Port<T> inputPort, Port<U> outputPort, BiFunction<WorkflowState, T, U> actionFunction) {
		this.actionName = requireNonNull(actionName, "actionName must be non-null!");
		this.inputPort = requireNonNull(inputPort, "inputPort must be non-null!");
		this.outputPort = requireNonNull(outputPort, "outputPort must be non-null!");
		this.actionFlow = flow(inputPort, outputPort, actionFunction);
	}

	@Override
	public String name() {
		return actionName;
	}
	
	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return actionFlow.asTransition(owningStatemachine);
	}
	
	private Data<WorkflowState, Token> executeActionBehavior(Data<WorkflowState, Token> inputData){
		Statemachine<WorkflowState, Token> sm = inputData.state().statemachine();
		return actionFlowBehavior(sm).actOn(inputData);
	}

	private Behavior<WorkflowState, Token, Token> actionFlowBehavior(Statemachine<WorkflowState, Token> sm) {
		Behavior<WorkflowState, Token, Token> actionFlowBehavior = actionFlow.asTransition(sm).asBehavior(sm);
		return actionFlowBehavior;
	}
	
	private boolean areTokensInInputOrOutputPort(WorkflowState state) {
		return state.areTokensIn(inputPort) || state.areTokensIn(outputPort);
	}

	@Override
	public String toString() {
		return "ActionNode[" + name() + "]";
	}
}
