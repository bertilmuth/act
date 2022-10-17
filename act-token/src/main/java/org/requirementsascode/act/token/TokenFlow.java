package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Workflow.workflow;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow implements Flow<Workflow, Token>{
	private final State<Workflow, Token> fromAction;
	private final State<Workflow, Token> toAction;

	private TokenFlow(State<Workflow, Token> fromAction, State<Workflow, Token> toAction) {
		this.fromAction = requireNonNull(fromAction, "fromAction must be non-null!");
		this.toAction = requireNonNull(toAction, "toAction must be non-null!");
	}
	
	public static TokenFlow tokenFlow(State<Workflow, Token> fromAction, State<Workflow, Token> toAction) {		
		return new TokenFlow(fromAction, toAction);
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(fromAction, toAction, d -> transmit(d, fromAction, toAction));
	}
	
	private static Data<Workflow, Token> transmit(Data<Workflow, Token> d, State<Workflow, Token> sourceState, State<Workflow, Token> targetState) {
		assert(d.value().isPresent());
		Tokens tokensBefore = d.state().tokens();
		Token token = d.value().get();
		Tokens tokensAfter = tokensBefore.moveToken(token, targetState);
		return data(workflow(d.state().statemachine(), tokensAfter, null));
	}
}
