package org.requirementsascode.act.token;

import static org.requirementsascode.act.token.Token.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.statemachine.State;

public class TransmitTokens<V> implements BiFunction<Tokens<V>, V, Tokens<V>>{
	private final State<?, V> sourceState;
	private final State<?, V> targetState;
	
	public TransmitTokens(State<?, V> sourceState, State<?, V> targetState) {
		this.sourceState = sourceState;
		this.targetState = targetState;
	}

	public static <V> TransmitTokens<V> transmitTokens(State<?, V> sourceState, State<?, V> targetState) {
		return new TransmitTokens<>(sourceState, targetState);
	}
	
	@Override
	public Tokens<V> apply(Tokens<V> tokens, V value) {
		return tokens.moveToken(token(value, sourceState), targetState);
	}
}
