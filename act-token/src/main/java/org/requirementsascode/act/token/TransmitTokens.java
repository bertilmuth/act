package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.token.Token.token;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Transition;

public class TransmitTokens<V> implements Behavior<Tokens<V>, V, V>{
	private final State<?, V> sourceState;
	private final State<?, V> targetState;
	
	public TransmitTokens(State<?, V> sourceState, State<?, V> targetState) {
		this.sourceState = sourceState;
		this.targetState = targetState;
	}

	public static <V> Transition<Tokens<V>,V> transmitTokens(State<Tokens<V>, V> sourceState, State<Tokens<V>, V> targetState) {
		return transition(sourceState, targetState, new TransmitTokens<>(sourceState, targetState));
	}

	@Override
	public Data<Tokens<V>, V> actOn(Data<Tokens<V>, V> before) {
		assert(before.value().isPresent());
		Tokens<V> tokensBefore = before.state();
		V beforeValue = before.value().get();
		Tokens<V> tokensAfter = tokensBefore.moveToken(token(beforeValue, sourceState), targetState);
		return data(tokensAfter, beforeValue);
	}
}
