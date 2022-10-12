package org.requirementsascode.act.places;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.requirementsascode.act.statemachine.State;

public class Place<S, V> {
	private State<S, V> state;
	private LinkedList<V> tokens;

	private Place(State<S, V> state, List<V> tokens) {
		requireNonNull(state, "state must be non-null!");
		requireNonNull(tokens, "tokens must be non-null!");
		this.state = state;
		this.tokens = newTokenList(tokens);
	}

	public static <S, V> Place<S, V> forState(State<S, V> state) {
		return new Place<>(state, Collections.emptyList());
	}

	public Place<S, V> withTokens(List<V> tokens) {
		return new Place<>(state, tokens);
	}

	public State<S, V> state() {
		return state;
	}

	public int size() {
		return tokens.size();
	}
	
	Place<S, V> addToken(V token) {
		requireNonNull(token, "token must be non-null!");
		List<V> tokenList = new LinkedList<>(tokens);
		tokenList.add(token);
		return new Place<>(state, tokenList);
	}
	
	Place<S,V> next() {
		LinkedList<V> tokenList = new LinkedList<>(tokens);
		tokenList.pop();
		return new Place<>(state, tokenList);
	}

	Optional<V> token() {
		Optional<V> nextToken = Optional.ofNullable(tokens.peek());
		return nextToken;
	}

	private LinkedList<V> newTokenList(List<V> tokens) {
		return new LinkedList<>(tokens);
	}
}
