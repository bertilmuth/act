package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.Workflow.workflow;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow implements Flow<Workflow, ActionData>{
	private final State<Workflow, ActionData> sourceState;
	private final State<Workflow, ActionData> targetState;

	private TokenFlow(State<Workflow, ActionData> sourceState, State<Workflow, ActionData> targetState) {
		this.sourceState = requireNonNull(sourceState, "sourceState must be non-null!");
		this.targetState = requireNonNull(targetState, "targetState must be non-null!");
	}
	
	public static TokenFlow tokenFlow(State<Workflow, ActionData> sourceState, State<Workflow, ActionData> targetState) {		
		return new TokenFlow(sourceState, targetState);
	}

	@Override
	public Transition<Workflow, ActionData> asTransition(Statemachine<Workflow, ActionData> owningStatemachine) {
		return transition(sourceState, targetState, d -> transmit(d, sourceState, targetState));
	}
	
	private static Data<Workflow, ActionData> transmit(Data<Workflow, ActionData> d, State<Workflow, ActionData> sourceState, State<Workflow, ActionData> targetState) {
		assert(d.value().isPresent());
		Tokens tokensBefore = d.state().tokens();
		ActionData beforeValue = d.value().get();
		Tokens tokensAfter = tokensBefore.moveToken(token(sourceState, beforeValue), targetState);
		return data(workflow(tokensAfter));
	}
}
