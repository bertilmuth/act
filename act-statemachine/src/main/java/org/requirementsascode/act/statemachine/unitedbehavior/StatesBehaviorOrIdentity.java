package org.requirementsascode.act.statemachine.unitedbehavior;

import static java.util.Arrays.asList;
import static org.requirementsascode.act.core.Behavior.identity;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.merge.FirstOneWhoActsWins;

public class StatesBehaviorOrIdentity {
	public static <S,V0> Behavior<S, V0, V0> statesBehaviorOrIdentity(Statemachine<S,V0> statemachine) {
		Behavior<S, V0, V0> statesBehavior = statemachine.states().asBehavior(statemachine);
		return unitedBehavior(new FirstOneWhoActsWins<>(), asList(statesBehavior, identity()));
	}
}
