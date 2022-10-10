package org.requirementsascode.act.places;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.requirementsascode.act.statemachine.State;

public class Place<S, V> {
	private State<S,V> state;
	private List<V> tokens;

	private Place(State<S, V> state) {
		this.state = state;
		this.tokens = new ArrayList<>();
	}

	public static <S,V> Place<S, V> forState(State<S, V> state) {
		return new Place<>(state);
	}
	
	public void put(V token) {	
		tokens.add(token);
	}

	public State<S,V> state() {
		return state;
	}

	public List<V> tokens() {
		return unmodifiableList(tokens);
	}
}
