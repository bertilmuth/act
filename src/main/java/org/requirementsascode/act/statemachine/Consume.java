package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;

public class Consume{
	public static <S,V> Behavior<S,V> consume(BiFunction<S,V,S> consumer){
		requireNonNull(consumer, "consumer must be non-null!");
		return i -> data(consumer.apply(i.state(), i.value()), i.value());
	}
}