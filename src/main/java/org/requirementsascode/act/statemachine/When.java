package org.requirementsascode.act.statemachine;

import java.util.function.Predicate;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class When {
	public static <S, V1 extends V0, V2 extends V0, V0> Behavior<S,V0,V0> when(Class<V1> expectedTriggerType, Behavior<S, V1,V2> behavior) {
		Predicate<Data<S,V1>> predicate = d -> true;
		return WhenInCase.whenInCase(expectedTriggerType, predicate, behavior);
	}
}
