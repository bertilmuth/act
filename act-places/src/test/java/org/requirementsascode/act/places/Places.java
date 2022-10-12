package org.requirementsascode.act.places;

import java.util.ArrayList;
import java.util.List;

import org.requirementsascode.act.statemachine.Statemachine;

public class Places<S, V> {

	public static <S,V> Places<S, V> forStatemachine(Statemachine<S, V> statemachine) {
		return new Places<>();
	}

	public List<Place<S,V>> asList() {
		return new ArrayList<>();
	}

}
