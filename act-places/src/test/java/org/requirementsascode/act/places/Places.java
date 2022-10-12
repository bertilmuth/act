package org.requirementsascode.act.places;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Places<S, V> {
	private final Statemachine<S, V> statemachine;
	private final List<Place<S, V>> places;

	private Places(Statemachine<S,V> statemachine) {
		this.statemachine = statemachine;
		List<State<S, V>> states = statemachine.states().asList();
		places = states.stream()
			.map(Place::forState)
			.collect(Collectors.toList());
	}

	public static <S,V> Places<S, V> forStatemachine(Statemachine<S, V> statemachine) {
		return new Places<>(statemachine);
	}

	public List<Place<S,V>> asList() {
		return places;
	}

}
