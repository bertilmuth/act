package org.requirementsascode.act.statemachine.function;

import org.requirementsascode.act.core.Behavior;

public class When {
	public static <S, V1 extends V0, V2 extends V0, V0> Behavior<S,V0,V0> when(Class<V1> expectedType, Behavior<S, V1,V2> behavior) {
		return WhenInCase.whenInCase(expectedType, d -> true, behavior);
	}
}
