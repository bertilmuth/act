package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Token.token;
import static org.requirementsascode.act.token.WorkflowState.workflowState;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow<V> implements Flow<WorkflowState<V>, V>{
	private final State<WorkflowState<V>, V> sourceState;
	private final State<WorkflowState<V>, V> targetState;

	private TokenFlow(State<WorkflowState<V>, V> sourceState, State<WorkflowState<V>, V> targetState) {
		this.sourceState = requireNonNull(sourceState, "sourceState must be non-null!");
		this.targetState = requireNonNull(targetState, "targetState must be non-null!");
	}
	
	public static <V> TokenFlow<V> tokenFlow(State<WorkflowState<V>, V> sourceState, State<WorkflowState<V>, V> targetState) {		
		return new TokenFlow<>(sourceState, targetState);
	}

	@Override
	public Transition<WorkflowState<V>, V> asTransition(Statemachine<WorkflowState<V>, V> owningStatemachine) {
		return transition(sourceState, targetState, d -> transmit(d, sourceState, targetState));
	}
	
	private static <V> Data<WorkflowState<V>, V> transmit(Data<WorkflowState<V>, V> d, State<WorkflowState<V>, V> sourceState, State<WorkflowState<V>, V> targetState) {
		assert(d.value().isPresent());
		Tokens<V> tokensBefore = d.state().tokens();
		V beforeValue = d.value().get();
		Tokens<V> tokensAfter = tokensBefore.moveToken(token(beforeValue, sourceState), targetState);
		return data(workflowState(tokensAfter), beforeValue);
	}
}
