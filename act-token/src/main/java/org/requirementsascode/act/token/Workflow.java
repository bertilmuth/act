package org.requirementsascode.act.token;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.Step.proceed;
import static org.requirementsascode.act.token.Token.token;

import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Workflow {
	private final WorkflowState state;
	private final Statemachine<Workflow, Token> statemachine;
	
	Workflow(Statemachine<Workflow, Token> statemachine, WorkflowState state) {
		this.statemachine = statemachine;
		this.state = state;
	}
	
	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public WorkflowState state() {
		return state;
	}
	
	public Workflow start(ActionData actionData) {
		return nextStep(actionData).nextStep();
	}
	
	public Workflow nextStep() {
		return nextStep(proceed);
	}
	
	static Workflow createInitialWorkflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		Statemachine<Workflow, Token> statemachine = statemachineWith(actions, tokenFlows, initialActions);		
		return new Workflow(statemachine, intialWorkflowState(statemachine));
	}
	
	private static WorkflowState intialWorkflowState(Statemachine<Workflow, Token> statemachine) {
		return new WorkflowState(statemachine, new Tokens(emptyList()), null);
	}

	private Workflow nextStep(ActionData actionData) {
		requireNonNull(actionData, "actionData must be non-null!");
		Data<Workflow, Token> trigger = actionTrigger(actionData);
		Workflow updatedWorkflow = statemachine().actOn(trigger).state();
		return updatedWorkflow;
	}

	private Data<Workflow, Token> actionTrigger(ActionData actionData) {
		DefaultNode defaultNode = new DefaultNode(statemachine());
		Data<Workflow, Token> trigger = data(this, token(defaultNode, actionData));
		return trigger;
	}
	
	@Override
	public String toString() {
		return "Workflow[" + state().tokens() + "]";
	}
	
	Statemachine<Workflow, Token> statemachine() {
		return statemachine;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<Workflow, Token> statemachineWith(Actions actions, TokenFlows tokenFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		Flow[] flowsArray = Stream.concat(initialActions.stream(), tokenFlows.stream())
			.toArray(Flow[]::new);
		
		Statemachine<Workflow, Token> statemachine = 
			Statemachine.builder()
				.states(actionsArray)
				.transitions(
					removeTokensWithoutActionData()
				)
				.flows(flowsArray)
				.build();
		return statemachine;
	}
	
	private static Transition<Workflow, Token> removeTokensWithoutActionData() {
		return transition(anyState(), anyState(), 
			whenInCase(Token.class, Workflow::hasNoActionData, Workflow::removeToken));
	}

	private static boolean hasNoActionData(Data<Workflow, Token> d) {
		return d.value().map(t -> !t.actionData().isPresent()).orElse(false);
	}

	private static Data<Workflow, Token> removeToken(Data<Workflow, Token> inputData) {
		WorkflowState workflowState = Workflow.from(inputData).state();
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, Token> resultWorkflowWithRemovedToken = workflowState.removeToken(token);
		return resultWorkflowWithRemovedToken;
	}
}