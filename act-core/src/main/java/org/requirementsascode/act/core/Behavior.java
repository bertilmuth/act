package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.Change.change;
import static org.requirementsascode.act.core.Data.data;

import java.util.Objects;

public interface Behavior<S, V1, V2> {
	Data<S, V2> actOn(Data<S, V1> d);

	default <V3> Behavior<S, V1, V3> andThen(Behavior<S, V2, V3> nextBehavior) {
		Objects.requireNonNull(nextBehavior, "nextBehavior must be non-null!");
		return before -> nextBehavior.actOn(actOn(before));
	}

	default Behavior<S, V1, V2> andHandleChangeWith(ChangeHandler<S, V1, V2> changeHandler) {
		Objects.requireNonNull(changeHandler, "changeHandler must be non-null!");
		return before -> {
			Data<S, V2> after = actOn(before);
			Change<S, V1, V2> change = change(before, after);
			return changeHandler.handleChange(change);
		};
	}
	
	static <S, V1,V2> Data<S,V2> noOp(Data<S,V1> d) {
		return data(d.state());
	}
	
	static <S, V1> boolean hasTrigger(Data<S, V1> d) {
		return d.value() != null;
	}

	static <S, V1> Behavior<S, V1, V1> identity() {
		return d -> d;
	}
}