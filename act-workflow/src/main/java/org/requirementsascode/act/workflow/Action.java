package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Action implements Node{
	private final String name;
	private final Behavior<WorkflowState,Token,Token> actionBehavior;
	
	Action(String name, Behavior<WorkflowState,Token,Token> actionBehavior) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.actionBehavior = requireNonNull(actionBehavior, "actionBehavior must be non-null!");
	}	
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public State<WorkflowState, Token> asState() {		
		State<WorkflowState, Token> state = state(name(), s -> s.areTokensIn(this), 
			inCase(ConsumeToken::isContained, this::consumeToken));
		return state;
	}
	
	Data<WorkflowState, Token> consumeToken(Data<WorkflowState,Token> inputData) {
		WorkflowState state = inputData.state();
		List<Token> tokensInAction = state.tokensIn(this).collect(Collectors.toList());
		Data<WorkflowState, Token> result = data(state, token(this, actionOutputIn(state)));
				
		for (Token token : tokensInAction) {
			result = actionBehavior.actOn(data(result.state(), token));
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
