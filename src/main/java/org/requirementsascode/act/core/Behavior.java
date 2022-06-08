package org.requirementsascode.act.core;

import java.util.Objects;

public interface Behavior<S, V> {
	Data<S,V> actOn(Data<S,V> input);

	default Behavior<S, V> andThen(Behavior<S, V> nextBehavior){
    Objects.requireNonNull(nextBehavior);    
    return input -> nextBehavior.actOn(actOn(input));
	}
	
	static <S,V> Behavior<S, V> identity() {
    return d -> d;
  }
}