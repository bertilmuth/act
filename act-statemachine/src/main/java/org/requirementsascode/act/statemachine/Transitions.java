package org.requirementsascode.act.statemachine;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.statemachine.merge.OnlyOneBehaviorMayAct;

public class Transitions<S, V0> implements AsBehavior<S, V0>{
	private final List<Transition<S, V0>> transitions;

	private Transitions(List<Transition<S, V0>> transitions) {
		this.transitions = requireNonNull(transitions, "transitions must be non-null!");
	}

	static <S, V0> Transitions<S, V0> transitions(List<Transition<S, V0>> transitions) {
		return new Transitions<>(transitions);
	}
	
	@Override
	public Behavior<S, V0, V0> asBehavior(Statemachine<S, V0> owningStatemachine) {
		return unitedBehavior(new OnlyOneBehaviorMayAct<>(), transitionBehaviors(owningStatemachine));
	}
	
	private List<Behavior<S, V0, V0>> transitionBehaviors(Statemachine<S, V0> owningStatemachine) {
		List<Behavior<S, V0, V0>> behaviors = asList().stream().map(e -> e.asBehavior(owningStatemachine))
				.collect(Collectors.toList());
		return behaviors;
	}
	
	public List<Transition<S, V0>> asList() {
		return transitions;
	}
}
