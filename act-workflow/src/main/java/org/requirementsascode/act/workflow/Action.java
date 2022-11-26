package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final ActionBehavior actionBehavior;
	
	Action(String name, ActionBehavior actionBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
	}	
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public State<WorkflowState, Token> asState() {		
		State<WorkflowState, Token> state = state(name(), s -> s.hasTokens(this), when(Token.class, actionBehavior.asBehavior(this)));
		return state;
	}
	
	Data<WorkflowState, Token> consumeToken(Data<WorkflowState,Token> inputData, Behavior<WorkflowState, Token, Token> stepBehavior) {
		WorkflowState workflowState = inputData.state();
		List<Token> tokensInAction = workflowState.tokensIn(this).collect(Collectors.toList());
		Data<WorkflowState, Token> result = data(workflowState, token(this, actionOutputIn(workflowState)));
				
		for (Token token : tokensInAction) {
			result = stepBehavior.actOn(data(result.state(), token));
			if(hasStepActed(result)) {
				break;
			}
		}
		
		return result;
	}
	
	private ActionData actionOutputIn(WorkflowState workflowState) {
		return workflowState.actionOutput().orElse(null);
	}
	
	private boolean hasStepActed(Data<WorkflowState, Token> result) {
		return result.value().map(t -> t.actionData().isPresent()).orElse(false);
	}

	@Override
	public String toString() {
		return "Action[" + name + "]";
	}
}
