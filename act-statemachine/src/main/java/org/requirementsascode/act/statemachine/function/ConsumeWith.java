package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;

public class ConsumeWith{
	public static <S,V> Behavior<S,V,V> consumeWith(BiFunction<S,V,S> consumer){
		requireNonNull(consumer, "consumer must be non-null!");
		return d -> {
			assert(d.value().isPresent());
			V trigger = d.value().get();
			S newState = consumer.apply(d.state(), trigger);
			return data(newState, trigger);
		};
	}
}