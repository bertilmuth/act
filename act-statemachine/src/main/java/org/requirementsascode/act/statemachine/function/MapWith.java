package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class MapWith{
	public static <S,V1,V2> Behavior<S,V1,V2> mapWith(BiFunction<S,V1,Data<S,V2>> mapper){
		requireNonNull(mapper, "mapper must be non-null!");
		return d -> mapper.apply(d.state(), d.value());
	}
}