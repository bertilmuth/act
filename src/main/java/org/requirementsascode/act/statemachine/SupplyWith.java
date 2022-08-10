package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class SupplyWith{
	public static <S,V> Behavior<S,V,V> supplyWith(Function<S, Data<S,V>> supplier){
		requireNonNull(supplier, "supplier must be non-null!");
		return d -> supplier.apply(d.state());
	}
	
	public static <S,V> Behavior<S,V,V> supplyWith(BiFunction<S,V,Data<S,V>> supplier){
		requireNonNull(supplier, "supplier must be non-null!");
		return d -> supplier.apply(d.state(), d.value());
	}
}