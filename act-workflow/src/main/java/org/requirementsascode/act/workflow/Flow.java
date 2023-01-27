package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.workflow.behavior.ActionBehavior;

public class Flow<T extends ActionData, U extends ActionData> implements Part{
	private final Class<T> type;
	private final Ports inPorts;
	private final Ports outPorts;
	private final ActionBehavior<T, U> actionBehavior;

	Flow(Ports inPorts, Ports outPorts, Class<T> type, BiFunction<WorkflowState, T, U> actionFunction) {
		this(inPorts, outPorts, type, new ActionBehavior<>(type, inPorts, outPorts, actionFunction));
	}
	
	Flow(Ports inPorts, Ports outPorts, Class<T> type, ActionBehavior<T, U> actionBehavior) {
		this.type = requireNonNull(type, "type must be non-null!");
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
	}
	
	public Class<T> type(){
		return type;
	}
	
	@Override
	public Ports inPorts() {
		return inPorts;
	}
	
	@Override
	public Ports outPorts() {
		return outPorts;
	}

	@Override
	public Transition<WorkflowState, Token> asTransition(Statemachine<WorkflowState, Token> owningStatemachine) {
		return transition(inPorts().asOneState(), outPorts().asOneState(), actionBehavior);
	}
}
