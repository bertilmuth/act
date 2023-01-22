package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.Optional;
import java.util.function.BiFunction;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Flow<T extends ActionData, U extends ActionData> implements ExecutableNode{
	private final Class<T> type;
	private final Ports inPorts;
	private final Ports outPorts;
	private final BiFunction<WorkflowState, T, U> actionFunction;

	Flow(Class<T> type, Ports inPorts, Ports outPorts, BiFunction<WorkflowState, T, U> actionFunction) {
		this.type = requireNonNull(type, "type must be non-null!");
		this.inPorts = requireNonNull(inPorts, "inPorts must be non-null!");
		this.outPorts = requireNonNull(outPorts, "outPorts must be non-null!");
		this.actionFunction = requireNonNull(actionFunction, "actionFunction must be non-null!");
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
		Token token = firstTokenWithType(inputData.state(), type());

		WorkflowState stateAfterRemoval = removeTokenFromInputPorts(inPorts(), inputData.state());
		Data<WorkflowState, Token> functionInputData = data(stateAfterRemoval, token);
		Data<WorkflowState, Token> functionOutputData = transform(functionInputData);
		Data<WorkflowState, Token> outputData = addTokenToOutputPort(outPorts(), functionOutputData);
		return outputData;
	}
	
	private Token firstTokenWithType(WorkflowState state, Class<T> actionDataType) {		
		return inPorts().stream()
			.flatMap(p -> p.tokens(state))
			.filter(t -> t.actionData().isPresent())
			.filter(t -> actionDataType.isAssignableFrom(t.actionData().get().getClass()))
			.findFirst()
			.orElse(token(null));
	}
	
	private WorkflowState removeTokenFromInputPorts(Ports inputPorts, WorkflowState state) {
	    return inputPorts.stream()
	        .reduce(state, 
	        	(updatedState, port) -> removeTokenFromInputPort(port, updatedState), 
	        	(data1, data2) -> data2);
	} 

	private WorkflowState removeTokenFromInputPort(Port<?> inputPort, WorkflowState state) {
		Token inputToken = state.firstTokenIn(inputPort).get();
		WorkflowState updatedState = state.removeToken(inputPort, inputToken).state();
		return updatedState;
	}
	
	@SuppressWarnings("unchecked")
	private Data<WorkflowState, Token> transform(Data<WorkflowState, Token> inputData) {
		assert(inputData.value().isPresent());
		WorkflowState state = inputData.state();
		Optional<Token> token = inputData.value();
		
		U outActionData = token.get().actionData()
			.map(ad -> applyActionFunction(state, (T)ad))
			.orElse(null);
		
		return data(state, token(outActionData));
	}
	
	private Data<WorkflowState, Token> addTokenToOutputPort(Ports outPorts, Data<WorkflowState, Token> data) {
		return data.state().addToken(outPorts.stream().findFirst().get(), Token.from(data));
	}

	private U applyActionFunction(WorkflowState state, T actionData) {
		return actionFunction.apply(state, actionData);
	}
}
