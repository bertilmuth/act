package org.requirementsascode.act.places;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class Places<S, V> {
	private final Map<State<S, V>, Place<S, V>> stateToPlaceMap;

	private Places(Statemachine<S,V> statemachine) {
		this(mapStatesToPlaces(statemachine));
	}
	
	private Places(Map<State<S, V>, Place<S, V>> stateToPlaceMap) {
		this.stateToPlaceMap = stateToPlaceMap;
	}

	public static <S,V> Places<S, V> forStatemachine(Statemachine<S, V> statemachine) {
		return new Places<>(statemachine);
	}
	
	public Places<S, V> placeTokens(State<S, V> state, List<V> tokenList) {
		Place<S, V> updatedPlace = Place.forState(state).withTokens(tokenList);

		Map<State<S, V>, Place<S, V>> newStateToPlaceMap = new HashMap<>();
		newStateToPlaceMap.putAll(stateToPlaceMap);
		newStateToPlaceMap.replace(state, updatedPlace);
		return new Places<S,V>(newStateToPlaceMap);
	}

	public List<Place<S,V>> asList() {
		return new ArrayList<>(stateToPlaceMap.values());
	}
	
	public Optional<Place<S, V>> findByState(State<S, V> state) {
		Optional<Place<S, V>> place = Optional.ofNullable(stateToPlaceMap.get(state));
		return place;
	}

	private static <S,V> Map<State<S,V>, Place<S, V>> mapStatesToPlaces(Statemachine<S, V> statemachine) {
		List<State<S, V>> states = statemachine.states().asList();
		Map<State<S, V>, Place<S, V>> stateToPlaceMap = states.stream()
			.collect(toMap(Function.identity(), Place::forState));
		
		return stateToPlaceMap;
	}
}
