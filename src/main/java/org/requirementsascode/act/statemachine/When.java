package org.requirementsascode.act.statemachine;

import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class When {
	public static <S, V extends V0, V0> Behavior<S, V0> when(Class<V> expectedTriggerType, Behavior<S, V> behavior) {
		return inCase(typeMatches(expectedTriggerType), i -> behaviorActOn(behavior, i));
	}

	private static <S, V extends V0, V0> Predicate<Data<S, V0>> typeMatches(Class<V> expectedTriggerType) {
		Predicate<Data<S, V0>> predicate = i -> i.getValue() != null && hasExpectedType(triggerTypeOf(i), expectedTriggerType);
		return predicate;
	}

	@SuppressWarnings("unchecked")
	private static <S, V extends V0, V0> Data<S, V0> behaviorActOn(Behavior<S, V> behavior, Data<S, V0> input) {
		Data<S, V0> output = (Data<S, V0>) behavior.actOn((Data<S, V>) input);
		return output;
	}

	private static <V> boolean hasExpectedType(Class<? extends Object> inputTriggerType, Class<V> expectedTriggerType) {
		return expectedTriggerType.isAssignableFrom(inputTriggerType);
	}

	private static Class<? extends Object> triggerTypeOf(Data<?, ?> i) {
		return i.getValue().getClass();
	}
}
