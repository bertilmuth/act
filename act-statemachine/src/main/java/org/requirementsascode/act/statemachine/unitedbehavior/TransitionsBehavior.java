package org.requirementsascode.act.statemachine.unitedbehavior;

import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Transitions;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class TransitionsBehavior {
	public static <S,V0> Behavior<S, V0, V0> transitionsBehavior(Transitions<S, V0> transitions) {
		return unitedBehavior(new OnlyOneBehaviorMayAct<>(), transitions.asList());
	}
}
