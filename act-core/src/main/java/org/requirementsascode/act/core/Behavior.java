package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.Change.change;

import java.util.Objects;

public interface Behavior<S, V1, V2> {
	Data<S,V2> actOn(Data<S,V1> before);

	default <V3> Behavior<S, V1, V3> andThen(Behavior<S, V2, V3> nextBehavior){
    Objects.requireNonNull(nextBehavior);    
    return before -> nextBehavior.actOn(actOn(before));
	}
	
	default Behavior<S, V1, V2> andHandleChangeWith(HandleChange<S, V1, V2> changeHandler){
		return before -> {
			Data<S, V2> after = actOn(before);
			Change<S, V1, V2> change = change(before, after);
			return changeHandler.handleChange(change);
		};
	}
	
	static <S,V1> Behavior<S, V1, V1> identity() {
    return d -> d;
  }
}