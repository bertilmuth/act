package org.requirementsascode.act.statemachine.unitedbehavior;

import static java.util.Arrays.asList;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class StatesBehaviorOrIdentity {
	public static <S,V0> Behavior<S, V0, V0> statesBehaviorOrIdentity(List<State<S, V0>> states) {
		Behavior<S, V0, V0> statesBehavior = unitedBehavior(new OnlyOneBehaviorMayAct<>(), states);
		return unitedBehavior(new FirstOneWhoActsWins<>(), asList(statesBehavior, identity()));
	}
}
