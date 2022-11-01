package org.requirementsascode.act.token;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.token.RemoveTokensWithoutActionData.removeTokensWithoutActionData;
import static org.requirementsascode.act.token.Step.stepTrigger;
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
	
	private Workflow(Statemachine<Workflow, Token> statemachine, Tokens tokens, ActionData actionOutput) {
		this.statemachine = statemachine;
		this.state = new WorkflowState(this, tokens, actionOutput);
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
		return nextStep(stepTrigger);
	}
	
	static Workflow initialWorkflow(Actions actions, TokenFlows tokenFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(tokenFlows, "tokenFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		return createWorkflow(statemachineWith(actions, tokenFlows, initialActions), new Tokens(emptyList()), null);
	}

	static Workflow createWorkflow(Statemachine<Workflow, Token> statemachine, Tokens tokens, ActionData outputActionData) {
		return new Workflow(statemachine, tokens, outputActionData);
	}
	
	Data<Workflow, Token> removeToken(Token tokenBefore) {
		Tokens tokensAfter = state().tokens().removeToken(tokenBefore);
		return updatedData(tokensAfter, null);
	}
	
	private Data<Workflow, Token> updatedData(Tokens tokens, Token token) {
		ActionData outputActionData = token != null? token.actionData().orElse(null) : null;
		Workflow workflow = createWorkflow(statemachine(), tokens, outputActionData);
		return data(workflow, token);
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
		
		Flow[] flowsArray = Stream.concat(
			Stream.concat(initialActions.stream(), tokenFlows.stream()),
			Stream.of(removeTokensWithoutActionData()))
			.toArray(Flow[]::new);
		
		Statemachine<Workflow, Token> statemachine = 
			Statemachine.builder()
				.states(actionsArray)
				.transitions()
				.flows(flowsArray)
				.build();
		return statemachine;
	}
}

class RemoveTokensWithoutActionData implements Flow<Workflow, Token> {
	public static RemoveTokensWithoutActionData removeTokensWithoutActionData() {
		return new RemoveTokensWithoutActionData();
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(owningStatemachine.definedState(), owningStatemachine.definedState(), 
			whenInCase(Token.class, this::hasNoActionData, this::removeToken));
	}

	private boolean hasNoActionData(Data<Workflow, Token> d) {
		return d.value().map(t -> !t.actionData().isPresent()).orElse(false);
	}

	private Data<Workflow, Token> removeToken(Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, Token> resultWorkflowWithRemovedToken = workflow.removeToken(token);
		return resultWorkflowWithRemovedToken;
	}
}
