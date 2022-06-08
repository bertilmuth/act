package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;

public class Transit{
	public static <S,V> Behavior<S,V> transit(BiFunction<S,V,S> transit){
		requireNonNull(transit, "transit must be non-null!");
		return i -> data(transit.apply(i.getState(), i.getValue()), i.getValue());
	}
}