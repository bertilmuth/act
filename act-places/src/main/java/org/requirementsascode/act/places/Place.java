package org.requirementsascode.act.places;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.requirementsascode.act.statemachine.State;

public class Place<S, V> {
	private State<S,V> state;
	private List<V> tokens;

	private Place(State<S, V> state) {
		this(state, new ArrayList<>());
	}
	
	private Place(State<S, V> state, List<V> tokens) {
		this.state = state;
		this.tokens = tokens;
	}

	public static <S,V> Place<S, V> forState(State<S, V> state) {
		return new Place<>(state);
	}
	
	public Place<S, V> putToken(V token) {	
		List<V> updatedTokens = new ArrayList<>(tokens);
		updatedTokens.add(token);		
		return new Place<>(state, updatedTokens);
	}

	public State<S,V> state() {
		return state;
	}

	public List<V> tokens() {
		return unmodifiableList(tokens);
	}
}
