package org.requirementsascode.act.core;

import java.util.function.Predicate;

public class InCase<S,V>{
	public static <S,V> Behavior<S,V,V> inCase(Predicate<Data<S,V>> predicate, Behavior<S,V,V> behavior) {
		return inCase(predicate, behavior, new DoNothing<>());
	}
	
	public static <S,V> Behavior<S,V,V> inCase(Predicate<Data<S,V>> predicate, Behavior<S,V,V> behavior, Behavior<S,V,V> elseBehavior) {
		return before -> {
			Data<S,V> after = null;
			if (predicate.test(before)) {
				after = behavior.actOn(before);
			} else {
				after = elseBehavior.actOn(before);
			}
			return after;
		};
	}
}