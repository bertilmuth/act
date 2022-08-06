package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.Consume.consume;

import java.util.function.Function;

import org.requirementsascode.act.core.Behavior;

public class Init{
	public static <S,V> Behavior<S,V> init(Function<V,S> init){
		requireNonNull(init, "init must be non-null!");
		return consume((s,v) -> init.apply(v));
	}
}