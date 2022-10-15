package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Token.token;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class TokenFlow<V> implements Flow<Tokens<V>, V>{
	private final State<Tokens<V>, V> sourceState;
	private final State<Tokens<V>, V> targetState;

	private TokenFlow(State<Tokens<V>, V> sourceState, State<Tokens<V>, V> targetState) {
		this.sourceState = sourceState;
		this.targetState = targetState;
	}
	
	public static <V> TokenFlow<V> tokenFlow(State<Tokens<V>, V> sourceState, State<Tokens<V>, V> targetState) {		
		return new TokenFlow<>(sourceState, targetState);
	}

	@Override
	public Transition<Tokens<V>, V> asTransition(Statemachine<Tokens<V>, V> owningStatemachine) {
		return transition(sourceState, targetState, d -> transmit(d, sourceState, targetState));
	}
	
	private static <V> Data<Tokens<V>, V> transmit(Data<Tokens<V>, V> d, State<Tokens<V>, V> sourceState, State<Tokens<V>, V> targetState) {
		assert(d.value().isPresent());
		Tokens<V> tokensBefore = d.state();
		V beforeValue = d.value().get();
		Tokens<V> tokensAfter = tokensBefore.moveToken(token(beforeValue, sourceState), targetState);
		return data(tokensAfter, beforeValue);
	}
}
