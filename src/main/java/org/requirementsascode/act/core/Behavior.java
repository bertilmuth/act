package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.Change.change;
import java.util.Objects;

public interface Behavior<S, V> {
	Data<S,V> actOn(Data<S,V> before);

	default Behavior<S, V> andThen(Behavior<S, V> nextBehavior){
    Objects.requireNonNull(nextBehavior);    
    return before -> nextBehavior.actOn(actOn(before));
	}
	
	default Behavior<S, V> andHandleChangeWith(HandleChangeWith<S, V> changeHandler){
		return before -> {
			Data<S, V> after = actOn(before);
			changeHandler.handleChangeWith(change(before, after));
			return after;
		};
	}
	
	static <S,V> Behavior<S, V> identity() {
    return d -> d;
  }
}