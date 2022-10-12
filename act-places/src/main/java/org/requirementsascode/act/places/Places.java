package org.requirementsascode.act.places;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Places<S, V> {
	private final Map<State<S, V>, Place<S, V>> stateToPlaceMap;

	private Places(Statemachine<S,V> statemachine) {
		Objects.requireNonNull(statemachine, "statemachine must be non-null!");
		this.stateToPlaceMap = mapStatesToPlaces(statemachine);
	}

	public static <S,V> Places<S, V> forStatemachine(Statemachine<S, V> statemachine) {
		return new Places<>(statemachine);
	}
	
	public Optional<Place<S, V>> findByState(State<S, V> state) {
		Optional<Place<S, V>> place = asList().stream()
			.filter(p -> p.state().equals(state))
			.findFirst();
		
		return place;
	}
	
	public Places<S, V> updatePlace(State<S, V> state, List<V> tokenList) {
		/*Optional<Place<S, V>> updatedPlace = findByState(state)
			.map(p -> p.withTokens(tokenList));*/
		return null;
	}

	public List<Place<S,V>> asList() {
		return new ArrayList<>(stateToPlaceMap.values());
	}

	private Map<State<S,V>, Place<S, V>> mapStatesToPlaces(Statemachine<S, V> statemachine) {
		List<State<S, V>> states = statemachine.states().asList();
		Map<State<S, V>, Place<S, V>> stateToPlaceMap = states.stream()
			.collect(toMap(Function.identity(), Place::forState));
		
		return stateToPlaceMap;
	}
}
