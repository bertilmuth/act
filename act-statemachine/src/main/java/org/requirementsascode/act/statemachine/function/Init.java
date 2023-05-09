package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Init<S,V> implements Behavior<S,V,V>{
	private final ConsumeWith<S,V> initializer;

	public Init(Function<V, S> init){
		requireNonNull(init, "init must be non-null!");
		BiFunction<S, V, S> applyInitToValue = (s,v) -> init.apply(v);
		this.initializer = new ConsumeWith<>(applyInitToValue);
	}

	@Override
	public Data<S, V> actOn(Data<S, V> d) {
		return initializer.actOn(d);
	}
}