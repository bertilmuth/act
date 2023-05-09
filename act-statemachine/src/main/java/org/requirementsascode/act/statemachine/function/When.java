package org.requirementsascode.act.statemachine.function;

import static java.util.Objects.requireNonNull;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class When<S,V0> implements Behavior<S,V0,V0>{
	private Behavior<S, V0, V0> when;

	public <V1 extends V0, V2 extends V0> When(Class<V1> expectedType, Behavior<S, V1,V2> behavior) {
		requireNonNull(expectedType, "expectedType must be non-null!");
		requireNonNull(behavior, "behavior must be non-null!");
		this.when = new WhenInCase<>(expectedType, d -> true, behavior);
	}

	@Override
	public Data<S, V0> actOn(Data<S, V0> before) {
		return when.actOn(before);
	}
}
