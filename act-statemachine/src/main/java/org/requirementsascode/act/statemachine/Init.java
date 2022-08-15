package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.statemachine.ConsumeWith.consumeWith;

import java.util.function.Function;

import org.requirementsascode.act.core.Behavior;

public class Init{
	static <S,V> Behavior<S,V,V> init(Function<V,S> init){
		requireNonNull(init, "init must be non-null!");
		return consumeWith((s,v) -> init.apply(v));
	}
}