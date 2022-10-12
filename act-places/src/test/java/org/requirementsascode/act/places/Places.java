package org.requirementsascode.act.places;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Places<S, V> {
	private final List<Place<S, V>> places;

	private Places(Statemachine<S,V> statemachine) {
		Objects.requireNonNull(statemachine, "statemachine must be non-null!");
		this.places = createPlaces(statemachine);
	}

	public static <S,V> Places<S, V> forStatemachine(Statemachine<S, V> statemachine) {
		return new Places<>(statemachine);
	}
	
	public Optional<Place<Object, Object>> findByState(State<Integer, Integer> state1) {
		return Optional.empty();
	}

	public List<Place<S,V>> asList() {
		return places;
	}

	private List<Place<S, V>> createPlaces(Statemachine<S, V> statemachine) {
		List<State<S, V>> states = statemachine.states().asList();
		return states.stream()
			.map(Place::forState)
			.collect(Collectors.toList());
	}
}
