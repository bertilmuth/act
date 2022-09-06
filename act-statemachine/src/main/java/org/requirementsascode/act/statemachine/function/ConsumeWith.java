package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;

public class ConsumeWith{
	public static <S,V> Behavior<S,V,V> consumeWith(BiFunction<S,V,S> consumer){
		requireNonNull(consumer, "consumer must be non-null!");
		return d -> data(consumer.apply(d.state(), d.value()), d.value());
	}
}