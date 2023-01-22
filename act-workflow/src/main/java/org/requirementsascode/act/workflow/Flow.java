package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Flow<T extends ActionData, U extends ActionData> implements ExecutableNode{
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
		Token token = firstTokenWithType(inputData.state());
		Data<WorkflowState, Token> behaviorInputData = data(inputData.state(), token);
		Data<WorkflowState, Token> behaviorOutputData = actionBehavior.actOn(behaviorInputData);
		
		Data<WorkflowState, Token> afterRemoval = removeTokenFromInPorts(behaviorOutputData);
		Data<WorkflowState, Token> outputData = addTokenToOutPorts(afterRemoval);
		return outputData;
	}
	
	private Token firstTokenWithType(WorkflowState state) {		
		return inPorts().stream()
			.flatMap(p -> p.tokens(state))
			.filter(t -> t.actionData().isPresent())
			.filter(t -> type.isAssignableFrom(t.actionData().get().getClass()))
			.findFirst()
			.orElse(token(null));
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
	
	private Data<WorkflowState, Token> addTokenToOutPorts(Data<WorkflowState, Token> data) {
		return data.state().addToken(outPorts.stream().findFirst().get(), Token.from(data));
	}
}
