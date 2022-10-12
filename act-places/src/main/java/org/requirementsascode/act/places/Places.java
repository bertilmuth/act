package org.requirementsascode.act.places;

import static java.util.stream.Collectors.toMap;

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
	
	public Places<S, V> setTokens(State<S, V> state, List<V> tokenList) {
		Place<S, V> updatedPlace = Place.forState(state).withTokens(tokenList);
		Places<S, V> newPlaces = updatePlaceForState(state, updatedPlace);
		return newPlaces;
	}
	
	public Optional<V> nextToken(State<S, V> state) {
		return findByState(state).flatMap(Place::nextToken);
	}
	
	public Places<S, V> addToken(State<S, V> state, V token) {
		Optional<Place<S, V>> updatedPlace = findByState(state).map(p -> p.addToken(token));
		Places<S, V> newPlaces = updatePlaceForState(state, updatedPlace.get());
		return newPlaces;
	}
	
	private Places<S, V> updatePlaceForState(State<S, V> state, Place<S, V> updatedPlace) {
		Map<State<S, V>, Place<S, V>> newStateToPlaceMap = new HashMap<>();
		newStateToPlaceMap.putAll(stateToPlaceMap);
		newStateToPlaceMap.replace(state, updatedPlace);
		Places<S, V> newPlaces = new Places<S,V>(newStateToPlaceMap);
		return newPlaces;
	}
	
	private Optional<Place<S, V>> findByState(State<S, V> state) {
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
