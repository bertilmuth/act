package org.requirementsascode.act.core;

import java.util.function.Predicate;

public class InCase<S,V>{
	public static <S,V1, V2> Behavior<S,V1,V2> inCase(Predicate<Data<S,V1>> predicate, Behavior<S,V1,V2> behavior) {
		return inCase(predicate, behavior, Behavior::noOp);
	}
	
	public static <S,V1,V2> Behavior<S,V1,V2> inCase(Predicate<Data<S,V1>> predicate, Behavior<S,V1,V2> behavior, Behavior<S,V1,V2> elseBehavior) {
		return before -> {
			Data<S,V2> after = null;
			if (predicate.test(before)) {
				after = behavior.actOn(before);
			} else {
				after = elseBehavior.actOn(before);
			}
			return after;
		};
	}
}