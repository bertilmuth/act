package org.requirementsascode.act.statemachine.function;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class ConsumeWith<S,V> implements Behavior<S,V, V>{
	private final BiFunction<S, V, S> consumer;

	public ConsumeWith(BiFunction<S,V,S> consumer){
		this.consumer = consumer;
	}

	@Override
	public Data<S, V> actOn(Data<S, V> d) {
		assert(d.value().isPresent());
		V trigger = d.value().get();
		S newState = consumer.apply(d.state(), trigger);
		return data(newState, trigger);
	}
}