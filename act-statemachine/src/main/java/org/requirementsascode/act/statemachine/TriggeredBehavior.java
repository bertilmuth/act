package org.requirementsascode.act.statemachine;

import static org.requirementsascode.act.core.InCase.inCase;

import java.util.Objects;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class TriggeredBehavior<S,V0> implements Behavior<S, V0, V0> {
	private final Behavior<S, V0, V0> triggeredBehavior;

	public TriggeredBehavior(Behavior<S, V0, V0> behaviorToTrigger) {
		Objects.requireNonNull(behaviorToTrigger, "behaviorToTrigger must be non-null!");
		triggeredBehavior = inCase(this::triggerIsPresent, behaviorToTrigger);
	}
	
	@Override
	public Data<S, V0> actOn(Data<S, V0> d) {
		return triggeredBehavior.actOn(d);
	}
	
	private boolean triggerIsPresent(Data<?, ?> data) {
		return data.value().isPresent();
	}
}
