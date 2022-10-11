package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.requirementsascode.act.statemachine.State;

public class Place<S, V> {
	private State<S, V> state;
	private List<V> tokens;

	private Place(State<S, V> state, List<V> tokens) {
		this.state = state;
		this.tokens = newTokenList(tokens);
	}

	public static <S, V> Place<S, V> forState(State<S, V> state) {
		return new Place<>(state, Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	public Place<S, V> withTokens(V... tokens) {
		return new Place<>(state, asList(tokens));
	}

	public Place<S, V> putToken(V token) {
		List<V> tokenList = new ArrayList<>(tokens);
		tokenList.add(token);
		return new Place<>(state, tokenList);
	}
	
	public Optional<V> nextToken() {
		Optional<V> nextToken = tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
		nextToken.ifPresent(tokens::remove);
		return nextToken;
	}

	public State<S, V> state() {
		return state;
	}

	public Integer size() {
		return tokens.size();
	}

	public List<V> tokens() {
		return unmodifiableList(tokens);
	}
	
	private ArrayList<V> newTokenList(List<V> tokens) {
		return new ArrayList<>(tokens);
	}
}
