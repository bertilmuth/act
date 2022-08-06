package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class Supply{
	public static <S,V> Behavior<S,V> supply(BiFunction<S,V,Data<S,V>> supplier){
		requireNonNull(supplier, "supplier must be non-null!");
		return i -> supplier.apply(i.state(), i.value());
	}
}