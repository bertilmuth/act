package org.requirementsascode.act.core.testdata;

import static org.requirementsascode.act.core.Behavior.hasTrigger;
import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class On{
	public static <S, V extends V0, V0> Behavior<S, V0,V0> on(String expectedTriggerTypeName, Behavior<S, V,V> behavior){
		return inCase(typeMatches(expectedTriggerTypeName), behaviorV0(behavior));
	}
	
	private static <S, V extends V0, V0> Predicate<Data<S, V0>> typeMatches(String expectedTriggerTypeName) {
		Predicate<Data<S, V0>> predicate = d -> hasTrigger(d) && hasExpectedType(triggerTypeOf(d), expectedTriggerTypeName);
		return predicate;
	}
	
	private static <V> boolean hasExpectedType(String beforeTriggerTypeName, String expectedTriggerTypeName) {
		return expectedTriggerTypeName.equals(beforeTriggerTypeName);
	}
	
	private static String triggerTypeOf(Data<?, ?> i) {
		return i.value().getClass().getSimpleName();
	}
	
	@SuppressWarnings("unchecked")
	private static <S, V1 extends V0, V2 extends V0, V0> Behavior<S,V0,V0> behaviorV0(Behavior<S, V1,V2> behavior){
		return d -> (Data<S,V0>)behavior.actOn((Data<S,V1>)d);
	}
}
