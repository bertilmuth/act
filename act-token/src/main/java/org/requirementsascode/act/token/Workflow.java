package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.WorkflowState.intialWorkflowState;

import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Workflow {
	private final WorkflowState initialState;
	private final Statemachine<WorkflowState, Token> statemachine;
	
	Workflow(Statemachine<WorkflowState, Token> statemachine) {
		this.statemachine = statemachine;
		this.initialState = intialWorkflowState(statemachine);
	}
	
	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public Data<WorkflowState, ActionData> start(ActionData actionData) {
		Data<WorkflowState, ActionData> inputData = data(initialState, actionData);
		Data<WorkflowState, Token> output = statemachine().actOn(tokenized(inputData));
		return nextStep(output.state());
	}

	public Data<WorkflowState, ActionData> nextStep(WorkflowState workflowState) {
		return actOn(data(workflowState, Step.proceed));
	}
	
	public Data<WorkflowState, ActionData> actOn(Data<WorkflowState,ActionData> inputData) {
		Data<WorkflowState, Token> output = statemachine().actOn(tokenized(inputData));
		ActionData outputActionData = output.state().actionOutput().orElse(null);
		return data(output.state(), outputActionData);
	}

	private Data<WorkflowState, Token> tokenized(Data<WorkflowState,ActionData> inputData) {
		DefaultNode defaultNode = new DefaultNode(statemachine());
		Token token = token(defaultNode, inputData.value().orElse(null));
		Data<WorkflowState, Token> data = data(inputData.state(), token);
		return data;
	}
	
	static Workflow createInitialWorkflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		Statemachine<WorkflowState, Token> statemachine = statemachineWith(actions, tokenFlows, initialActions);		
		return new Workflow(statemachine);
	}
	
	Statemachine<WorkflowState, Token> statemachine() {
		return statemachine;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<WorkflowState, Token> statemachineWith(Actions actions, TokenFlows tokenFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		Flow[] flowsArray = Stream.concat(initialActions.stream(), tokenFlows.stream())
			.toArray(Flow[]::new);
		
		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.states(actionsArray)
				.transitions(
					removeTokensWithoutActionData()
				)
				.flows(flowsArray)
				.build();
		return statemachine;
	}
	
	private static Transition<WorkflowState, Token> removeTokensWithoutActionData() {
		return transition(anyState(), anyState(), 
			whenInCase(Token.class, Workflow::hasNoActionData, Workflow::removeToken));
	}

	private static boolean hasNoActionData(Data<WorkflowState, Token> d) {
		return d.value().map(t -> !t.actionData().isPresent()).orElse(false);
	}

	private static Data<WorkflowState, Token> removeToken(Data<WorkflowState, Token> inputData) {
		WorkflowState workflowState = inputData.state();
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<WorkflowState, Token> resultWorkflowWithRemovedToken = workflowState.removeToken(token);
		return resultWorkflowWithRemovedToken;
	}
}