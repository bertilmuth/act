package org.requirementsascode.act.core.testdata;

import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class On{
	public static <S, V extends V0, V0> Behavior<S, V0,V0> on(String expectedTriggerTypeName, Behavior<S, V,V> behavior){
		return inCase(typeMatches(expectedTriggerTypeName), i -> behaviorActOn(behavior, i));
	}
	
	private static <S, V extends V0, V0> Predicate<Data<S, V0>> typeMatches(String expectedTriggerTypeName) {
		Predicate<Data<S, V0>> predicate = i -> i.value() != null && hasExpectedType(triggerTypeOf(i), expectedTriggerTypeName);
		return predicate;
	}
	
	private static <V> boolean hasExpectedType(String beforeTriggerTypeName, String expectedTriggerTypeName) {
		return expectedTriggerTypeName.equals(beforeTriggerTypeName);
	}
	
	private static String triggerTypeOf(Data<?, ?> i) {
		return i.value().getClass().getSimpleName();
	}
	
	@SuppressWarnings("unchecked")
	private static <S, V extends V0, V0> Data<S, V0> behaviorActOn(Behavior<S, V,V> behavior, Data<S, V0> before) {
		Data<S, V0> after = (Data<S, V0>) behavior.actOn((Data<S, V>) before);
		return after;
	}
}
