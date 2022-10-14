package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.token.Token.token;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class TransmitTokens<V> implements Behavior<Tokens<V>, V, V>{
	private final State<?, V> sourceState;
	private final State<?, V> targetState;
	
	public TransmitTokens(State<?, V> sourceState, State<?, V> targetState) {
		this.sourceState = sourceState;
		this.targetState = targetState;
	}

	public static <V> TransmitTokens<V> transmitTokens(State<?, V> sourceState, State<?, V> targetState) {
		return new TransmitTokens<>(sourceState, targetState);
	}
	
	public Tokens<V> apply(Tokens<V> tokens, V value) {
		return tokens.moveToken(token(value, sourceState), targetState);
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
