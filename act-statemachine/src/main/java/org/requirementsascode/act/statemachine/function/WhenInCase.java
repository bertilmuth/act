package org.requirementsascode.act.statemachine.function;

import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class WhenInCase {
	@SuppressWarnings("unchecked")
	public static <S, V1 extends V0, V2 extends V0, V0> Behavior<S,V0,V0> whenInCase(Class<V1> expectedType, Predicate<Data<S,V1>> predicate, Behavior<S, V1,V2> behavior) {
		return (Behavior<S, V0,V0>) inCase(typeMatches(expectedType), inCase(predicate, i -> behaviorActOn(i, behavior)));
	}

	private static <S, V1 extends V0, V0> Predicate<Data<S, V0>> typeMatches(Class<V1> expectedType) {
		Predicate<Data<S, V0>> predicate = d -> d.value() != null && hasExpectedType(triggerTypeOf(d), expectedType);
		return predicate;
	}

	@SuppressWarnings("unchecked")
	private static <S, V1 extends V0, V2 extends V0, V0> Data<S, V0> behaviorActOn(Data<S, V1> before, Behavior<S, V1,V2> behavior) {
		Data<S, V0> after = (Data<S, V0>) behavior.actOn(before);
		return after;
	}

	private static <V> boolean hasExpectedType(Class<? extends Object> beforeType, Class<V> expectedType) {
		return expectedType.isAssignableFrom(beforeType);
	}

	private static Class<? extends Object> triggerTypeOf(Data<?, ?> d) {
		return d.value().getClass();
	}
}
