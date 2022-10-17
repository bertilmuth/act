package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Workflow.workflow;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow implements Flow<Workflow, Token>{
	private final Action fromAction;
	private final Action toAction;

	private TokenFlow(Action fromAction, Action toAction) {
		this.fromAction = requireNonNull(fromAction, "fromAction must be non-null!");
		this.toAction = requireNonNull(toAction, "toAction must be non-null!");
	}
	
	public static TokenFlow tokenFlow(Action fromAction, Action toAction) {		
		return new TokenFlow(fromAction, toAction);
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(fromAction.asState(), toAction.asState(), d -> transmit(d, fromAction, toAction));
	}
	
	private static Data<Workflow, Token> transmit(Data<Workflow, Token> d, Node fromNode, Node toNode) {
		assert(d.value().isPresent());
		Tokens tokensBefore = d.state().tokens();
		Token token = d.value().get();
		Tokens tokensAfter = tokensBefore.moveToken(token, toNode);
		return data(workflow(d.state().statemachine(), tokensAfter));
	}
}
