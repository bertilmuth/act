package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;
import static org.requirementsascode.act.workflow.WorkflowApi.*;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.workflow.behavior.ActionBehavior;

public class Flow<T extends ActionData, U extends ActionData> implements Part{
	private final Class<T> type;
	private final Ports inPorts;
	private final Ports outPorts;
	private final ActionBehavior<T, U> actionBehavior;

	Flow(Class<T> type, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this(type, inPorts, outPorts, new ActionBehavior<>(actionFunction));
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
		return selectOneTokenByType(inPorts(), type())
				.andThen(actionBehavior)
				.andThen(this::removeTokenFromInPorts)
				.andThen(this::addTokensToOutPorts)
				.actOn(inputData);
	}
	
	private Behavior<WorkflowState, Token, Token> selectOneTokenByType(Ports ports, Class<T> type) {
		return d -> {
			WorkflowState state = d.state();
			Token outToken = ports.stream()
				.flatMap(p -> p.tokens(state))
				.filter(t -> t.actionData().isPresent())
				.filter(t -> type.isAssignableFrom(t.actionData().get().getClass()))
				.findFirst()
				.orElse(emptyToken());
			return data(state, outToken);
		};
	}
	
	private Data<WorkflowState, Token> removeTokenFromInPorts(Data<WorkflowState, Token> inputData) {
		WorkflowState outputState = inPorts().stream()
	        .reduce(inputData.state(), 
	        	(s, port) -> removeTokenFromInputPort(s, port), 
	        	(data1, data2) -> data2);
		return data(outputState, Token.from(inputData));
	} 

	private WorkflowState removeTokenFromInputPort(WorkflowState state, Port<?> inputPort) {
		Token inputToken = state.firstTokenIn(inputPort).get();
		WorkflowState updatedState = state.removeToken(inputPort, inputToken).state();
		return updatedState;
	}
	
	private Data<WorkflowState, Token> addTokensToOutPorts(Data<WorkflowState, Token> data) {
		return data.state().addToken(outPorts.stream().findFirst().get(), Token.from(data));
	}
}
