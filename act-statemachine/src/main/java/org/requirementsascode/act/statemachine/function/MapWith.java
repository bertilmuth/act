package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class MapWith<S,V1,V2> implements Behavior<S,V1,V2>{
	private final BiFunction<S, V1, Data<S, V2>> mapper;

	public MapWith(BiFunction<S,V1,Data<S,V2>> mapper){
		this.mapper = requireNonNull(mapper, "mapper must be non-null!");
	}

	@Override
	public Data<S, V2> actOn(Data<S, V1> d) {
		assert(d.value().isPresent());
		V1 trigger = d.value().get();
		Data<S, V2> newData = mapper.apply(d.state(), trigger);
		
		return newData;
	}
}