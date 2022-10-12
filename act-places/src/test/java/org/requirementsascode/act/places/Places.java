package org.requirementsascode.act.places;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Places<S, V> {
	private final Statemachine<S, V> statemachine;

	private Places(Statemachine<S,V> statemachine) {
		this.statemachine = statemachine;
	}

	public static <S,V> Places<S, V> forStatemachine(Statemachine<S, V> statemachine) {
		return new Places<>(statemachine);
	}

	public List<Place<S,V>> asList() {
		List<State<S, V>> states = statemachine.states().asList();
		
		List<Place<S, V>> places = states.stream()
			.map(Place::forState)
			.collect(Collectors.toList());

		return places;
	}

}
