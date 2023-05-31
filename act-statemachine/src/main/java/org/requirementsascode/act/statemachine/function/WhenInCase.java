package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Behavior.hasActed;
import static org.requirementsascode.act.core.InCase.inCase;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class WhenInCase<S,V0> implements Behavior<S,V0,V0> {
	private Behavior<S, V0, V0> whenInCase;

	public <V1 extends V0, V2 extends V0> WhenInCase(Class<V1> expectedType, Predicate<Data<S,V1>> predicate, Behavior<S, V1,V2> behavior) {
		requireNonNull(expectedType, "expectedType must be non-null!");
		requireNonNull(behavior, "behavior must be non-null!");		
		Behavior<S,V0,V0> conditionalBehavior = inCase(predicateV0(predicate), behaviorV0(behavior));
		this.whenInCase = inCase(typeMatches(expectedType), conditionalBehavior);
	}
	
	@Override
	public Data<S, V0> actOn(Data<S, V0> d) {
		return whenInCase.actOn(d);
	}
	
	@SuppressWarnings("unchecked")
	private <V1 extends V0> Predicate<Data<S,V0>> predicateV0(Predicate<Data<S,V1>> predicate){
		return d -> predicate.test((Data<S,V1>)d);
	}
	
	@SuppressWarnings("unchecked")
	private <V1 extends V0, V2 extends V0> Behavior<S,V0,V0> behaviorV0(Behavior<S, V1,V2> behavior){
		return d -> (Data<S,V0>)behavior.actOn((Data<S,V1>)d);
	}

	private static <S, V1 extends V0, V0> Predicate<Data<S, V0>> typeMatches(Class<V1> expectedType) {
		Predicate<Data<S, V0>> predicate = d -> hasActed(d) && hasExpectedType(valueTypeOf(d), expectedType);
		return predicate;
	}

	private static boolean hasExpectedType(Class<?> valueType, Class<?> expectedType) {
		return expectedType.isAssignableFrom(valueType);
	}

	private static Class<?> valueTypeOf(Data<?, ?> d) {
		return d.value().get().getClass();
	}
}
