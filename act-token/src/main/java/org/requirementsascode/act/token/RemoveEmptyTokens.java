package org.requirementsascode.act.token;

import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

class RemoveEmptyTokens implements Flow<Workflow, Token> {
	public static RemoveEmptyTokens removeEmptyTokens() {
		return new RemoveEmptyTokens();
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(owningStatemachine.definedState(), owningStatemachine.definedState(), 
			whenInCase(Token.class, this::tokenIsEmpty, this::removeToken));
	}

	private boolean tokenIsEmpty(Data<Workflow, Token> d) {
		return d.value().map(t -> !t.actionData().isPresent()).orElse(false);
	}

	private Data<Workflow, Token> removeToken(Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, Token> resultWorkflowWithRemovedToken = workflow.removeToken(token);
		return resultWorkflowWithRemovedToken;
	}
}
