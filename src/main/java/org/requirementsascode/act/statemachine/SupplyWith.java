package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class SupplyWith{
	public static <S,V1,V2> Behavior<S,V1,V2> supplyWith(BiFunction<S,V1,Data<S,V2>> supplier){
		requireNonNull(supplier, "supplier must be non-null!");
		return d -> supplier.apply(d.state(), d.value());
	}
}