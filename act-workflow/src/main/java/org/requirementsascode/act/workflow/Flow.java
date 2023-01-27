package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.workflow.behavior.ActionBehavior;

public class Flow<T extends ActionData, U extends ActionData> implements Part, Transitionable<WorkflowState, Token>{
	private final Part owner;
	private final Class<T> type;
	private final ActionBehavior<T, U> actionBehavior;
	
	Flow(Part owner, Class<T> type, BiFunction<WorkflowState, T, U> actionFunction) {
		this.owner = requireNonNull(owner, "owner must be non-null!");
		this.type = requireNonNull(type, "type must be non-null!");
		this.actionBehavior = new ActionBehavior<>(owner, type, actionFunction);
	}
	
	public Class<T> type(){
		return type;
	}

	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts().asOneState(), outPorts().asOneState(), actionBehavior);
	}

	@Override
	public Ports inPorts() {
		return owner.inPorts();
	}

	@Override
	public Ports outPorts() {
		return owner.outPorts();
	}
}
