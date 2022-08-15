package org.requirementsascode.act.statemachine.pbt;

import static org.requirementsascode.act.core.Data.data;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.state.Action;
import net.jqwik.api.state.Transformer;

public class StateMachineEvent<S, V1, V2> implements Action.Independent<S> {
	private final Arbitrary<? extends V1> triggers;
	private final Behavior<S, V1, V2> behavior;

	private StateMachineEvent(Arbitrary<? extends V1> triggers, Behavior<S, V1, V2> behavior) {
		this.triggers = triggers;
		this.behavior = behavior;
	}
	
	public static <S, V1, V2> StateMachineEvent<S, V1, V2> stateMachineEvent(Arbitrary<? extends V1> triggers, Behavior<S, V1, V2> behavior){
		return new StateMachineEvent<>(triggers, behavior);
	}

	@Override
	public Arbitrary<Transformer<S>> transformer() {
		return triggers.map(trigger -> Transformer.transform(
			trigger.toString(),
			beforeState -> {
				Data<S, V2> afterData = behavior.actOn(data(beforeState, trigger));
				return afterData.state();
			}
		));
	}
}
