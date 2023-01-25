package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.workflow.behavior.ActionBehavior;
import org.requirementsascode.act.workflow.behavior.RemoveTokensFromPorts;
import org.requirementsascode.act.workflow.behavior.SelectOneTokenByType;

public class Flow<T extends ActionData, U extends ActionData> implements Part{
	private final Class<T> type;
	private final Ports inPorts;
	private final Ports outPorts;
	private final ActionBehavior<T, U> actionBehavior;

	Flow(Class<T> type, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this(type, inPorts, outPorts, new ActionBehavior<>(type, actionFunction));
	}
	
	Flow(Class<T> type, Ports inPorts, Ports outPorts, ActionBehavior<T, U> actionBehavior) {
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
		return transition(inPorts().asOneState(), outPorts().asOneState(), this::transformAndMove);
	}
	
	private Data<WorkflowState, Token> transformAndMove(Data<WorkflowState, Token> inputData) {
		return new SelectOneTokenByType<>(inPorts(), type())
			.andThen(actionBehavior)
			.andThen(new RemoveTokensFromPorts(inPorts()))
			.andThen(this::addTokensToOutPorts)
			.actOn(inputData);
	}
	
	private Data<WorkflowState, Token> addTokensToOutPorts(Data<WorkflowState, Token> data) {
		return data.state().addToken(outPorts.stream().findFirst().get(), Token.from(data));
	}
}
