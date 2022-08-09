package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.Change.change;
import java.util.Objects;

public interface Behavior<S, V> {
	Data<S,V> actOn(Data<S,V> input);

	default Behavior<S, V> andThen(Behavior<S, V> nextBehavior){
    Objects.requireNonNull(nextBehavior);    
    return input -> nextBehavior.actOn(actOn(input));
	}
	
	default Behavior<S, V> andHandleChange(HandleChange<S, V> changeHandler){
		return input -> {
			Data<S, V> output = actOn(input);
			changeHandler.handleChange(change(input, output));
			return output;
		};
	}
	
	static <S,V> Behavior<S, V> identity() {
    return d -> d;
  }
}