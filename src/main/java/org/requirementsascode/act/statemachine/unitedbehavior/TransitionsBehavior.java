package org.requirementsascode.act.statemachine.unitedbehavior;

import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Transition;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class TransitionsBehavior {
	public static <S,V0> Behavior<S, V0> transitionsBehavior(List<Transition<S, ? extends V0, V0>> transitions) {
		return unitedBehavior(new OnlyOneBehaviorMayAct<>(), transitions);
	}
}
