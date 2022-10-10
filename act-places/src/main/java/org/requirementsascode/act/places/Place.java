package org.requirementsascode.act.places;

import org.requirementsascode.act.statemachine.State;

public class Place<S, V> {
	private State<S,V> state;

	public Place(State<S, V> state) {
		this.state = state;
	}

	public static <S,V> Place<S, V> forState(State<S, V> state) {
		return new Place<>(state);
	}

	public State<S,V> state() {
		return state;
	}

}
