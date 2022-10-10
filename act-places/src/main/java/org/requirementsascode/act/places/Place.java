package org.requirementsascode.act.places;

import java.util.Arrays;
import java.util.List;

import org.requirementsascode.act.statemachine.State;

public class Place<S, V> {
	private State<S,V> state;
	private V token;

	public Place(State<S, V> state) {
		this.state = state;
	}

	public static <S,V> Place<S, V> forState(State<S, V> state) {
		return new Place<>(state);
	}
	
	public void put(V token) {	
		this.token = token;
	}

	public State<S,V> state() {
		return state;
	}

	public List<V> tokens() {
		return Arrays.asList(token);
	}
}
