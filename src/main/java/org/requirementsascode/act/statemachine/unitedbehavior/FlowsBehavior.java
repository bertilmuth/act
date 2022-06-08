package org.requirementsascode.act.statemachine.unitedbehavior;

import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;
import static org.requirementsascode.act.statemachine.unitedbehavior.FlowsTransitions.flowsTransitions;

import java.util.List;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class FlowsBehavior {
	public static <S, V0> Behavior<S, V0> flowsBehavior(List<Flow<S, ? extends V0, V0>> flows, State<S, V0> definedState,
		State<S, V0> defaultState) {

		return unitedBehavior(new OnlyOneBehaviorMayAct<>(), flowsTransitions(flows, definedState, defaultState));
	}
}
