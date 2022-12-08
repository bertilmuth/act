package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.workflow.trigger.AddToken;

class AnyNode implements Node {
	AnyNode() {
	}

	@Override
	public String name() {
		return anyState().name();
	}

	@Override
	public State<WorkflowState, Token> asState() {
		State<WorkflowState, Token> state = state("Any Node", s -> true, anyNodeBehavior());
		return state;
	}
	
	private Behavior<WorkflowState, Token, Token> anyNodeBehavior() {
		Behavior<WorkflowState, Token, Token> behavior = 
			inCase(AddToken::isContained, d -> {
				Token tokenToAdd = Token.from(d).actionData().map(t -> (AddToken)t).map(AddToken::token).orElse(null);
				return d.state().addToken(tokenToAdd);
			});
		return behavior;
	}
}