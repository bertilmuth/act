package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow implements Flow<Workflow, Token>{
	private final Node fromAction;
	private final Node toAction;

	private TokenFlow(Node fromAction, Node toAction) {
		this.fromAction = requireNonNull(fromAction, "fromAction must be non-null!");
		this.toAction = requireNonNull(toAction, "toAction must be non-null!");
	}
	
	public static TokenFlow tokenFlow(Node fromAction, Node toAction) {		
		return new TokenFlow(fromAction, toAction);
	}

	@Override
	public Transition<Workflow, Token> asTransition(Statemachine<Workflow, Token> owningStatemachine) {
		return transition(fromAction.asState(), toAction.asState(), d -> transmit(d, fromAction, toAction));
	}
	
	private Data<Workflow, Token> transmit(Data<Workflow, Token> d, Node fromNode, Node toNode) {
		assert(d.value().isPresent());
		Workflow workflow = workflowOf(d);
		Token token = d.value().get();
		Tokens tokensAfter = workflow.tokens().moveToken(token, toNode);
		return dataAfter(d, tokensAfter, token);
	}
	
	private Data<Workflow, Token> dataAfter(Data<Workflow, ?> functionOutput, Tokens tokensAfter,
			Token tokenAfter) {
		Workflow newWorkflow = Workflow.workflow(functionOutput.state().statemachine(), tokensAfter);
		return data(newWorkflow, tokenAfter);
	}
	
	private Workflow workflowOf(Data<Workflow, ?> data) {
		return data.state();
	}
}
