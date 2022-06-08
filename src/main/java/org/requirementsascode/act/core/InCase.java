package org.requirementsascode.act.core;

import java.util.function.Predicate;

public class InCase<S,V>{
	public static <S,V> Behavior<S,V> inCase(Predicate<Data<S,V>> predicate, Behavior<S,V> behavior) {
		return inCase(predicate, behavior, new DoNothing<>());
	}
	
	public static <S,V> Behavior<S,V> inCase(Predicate<Data<S,V>> predicate, Behavior<S,V> behavior, Behavior<S,V> elseBehavior) {
		return input -> {
			Data<S,V> output = null;
			if (predicate.test(input)) {
				output = behavior.actOn(input);
			} else {
				output = elseBehavior.actOn(input);
			}
			return output;
		};
	}
}